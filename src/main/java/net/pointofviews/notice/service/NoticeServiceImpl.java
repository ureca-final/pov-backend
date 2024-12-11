package net.pointofviews.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.member.domain.MemberFcmToken;
import net.pointofviews.member.repository.MemberFcmTokenRepository;
import net.pointofviews.notice.domain.Notice;
import net.pointofviews.notice.domain.NoticeReceive;
import net.pointofviews.notice.domain.NoticeSend;
import net.pointofviews.notice.dto.request.CreateNoticeTemplateRequest;
import net.pointofviews.notice.dto.request.SendNoticeRequest;
import net.pointofviews.notice.dto.response.CreateNoticeTemplateResponse;
import net.pointofviews.notice.dto.response.ReadNoticeResponse;
import net.pointofviews.notice.exception.NoticeException;
import net.pointofviews.notice.repository.NoticeReceiveRepository;
import net.pointofviews.notice.repository.NoticeRepository;
import net.pointofviews.notice.repository.NoticeSendRepository;
import net.pointofviews.notice.utils.FcmUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    private static final int BATCH_SIZE = 1000;

    private final NoticeRepository noticeRepository;
    private final NoticeSendRepository noticeSendRepository;
    private final NoticeReceiveRepository noticeReceiveRepository;
    private final MemberFcmTokenRepository memberFcmTokenRepository;
    private final FcmUtil fcmUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public CreateNoticeTemplateResponse saveNoticeTemplate(UUID adminId, CreateNoticeTemplateRequest request) {
        Notice notice = Notice.builder()
                .memberId(adminId)
                .noticeType(request.noticeType())
                .noticeContent(request.content())
                .noticeTitle(request.title())
                .description(request.description())
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        return new CreateNoticeTemplateResponse(
                savedNotice.getId(),
                savedNotice.getNoticeTitle(),
                savedNotice.getNoticeContent(),
                savedNotice.getNoticeType(),
                savedNotice.getDescription(),
                savedNotice.isActive(),
                savedNotice.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void sendNotice(SendNoticeRequest request) {
        Notice noticeTemplate = noticeRepository.findByIdAndIsActiveTrue(request.noticeTemplateId())
                .orElseThrow(NoticeException.NoticeTemplateNotFoundException::new);

        String content = replaceTemplateVariables(noticeTemplate.getNoticeContent(), request.templateVariables());

        NoticeSend noticeSend = NoticeSend.builder()
                .notice(noticeTemplate)
                .noticeContentDetail(content)
                .isSucceed(true)
                .build();

        noticeSendRepository.save(noticeSend);

        // Redis에서 선호 장르 사용자 조회
        String genreKey = "genre:preferences:" + request.templateVariables().get("genre");
        Set<UUID> targetMembers = getTargetMembers(genreKey);

        if (targetMembers.isEmpty()) {
            throw new NoticeException.NoTargetMembersFoundException();
        }

        // 배치 처리
        List<UUID> memberList = new ArrayList<>(targetMembers);
        for (int i = 0; i < memberList.size(); i += BATCH_SIZE) {
            int end = Math.min(memberList.size(), i + BATCH_SIZE);
            List<UUID> batchMembers = memberList.subList(i, end);

            // FCM 토큰 일괄 조회
            List<MemberFcmToken> fcmTokens = memberFcmTokenRepository.findActiveTokensByMemberIds(batchMembers);

            if (fcmTokens.isEmpty()) {
                continue;
            }

            List<NoticeReceive> noticeReceives = new ArrayList<>();
            for (MemberFcmToken fcmToken : fcmTokens) {
                try {
                    fcmUtil.sendMessage(fcmToken.getFcmToken(), noticeTemplate.getNoticeTitle(), content);

                    NoticeReceive noticeReceive = NoticeReceive.builder()
                            .member(fcmToken.getMember())
                            .noticeSendId(noticeSend.getId())
                            .noticeContent(content)
                            .noticeTitle(noticeTemplate.getNoticeTitle())
                            .noticeType(noticeTemplate.getNoticeType())
                            .build();

                    noticeReceives.add(noticeReceive);
                } catch (NoticeException.NoticeSendFailedException e) {
                    log.error("Failed to send notification to member: {}", fcmToken.getMember().getId(), e);
                    noticeSend.setSucceed(false);
                }
            }

            try {
                if (!noticeReceives.isEmpty()) {
                    noticeReceiveRepository.saveAll(noticeReceives);
                }
            } catch (Exception e) {
                log.error("Failed to save notice receives", e);
                noticeSend.setSucceed(false);
                throw new NoticeException.NoticeReceiveSaveFailedException();
            }
        }
    }

    @Override
    public List<ReadNoticeResponse> findNotices(UUID memberId) {
        return noticeReceiveRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(receive -> new ReadNoticeResponse(
                        receive.getId(),
                        receive.getNoticeTitle(),
                        receive.getNoticeContent(),
                        receive.getNoticeType(),
                        receive.isRead(),
                        receive.getCreatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void updateNotice(UUID memberId, Long noticeId) {
        NoticeReceive noticeReceive = noticeReceiveRepository.findByIdAndMemberId(noticeId, memberId)
                .orElseThrow(NoticeException.NoticeNotFoundException::new);

        noticeReceive.setRead(true);
    }

    private String replaceTemplateVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private Set<UUID> getTargetMembers(String genreKey) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(genreKey);
            if (members == null) return new HashSet<>();

            return members.stream()
                    .map(member -> UUID.fromString(member.toString()))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Failed to get target members from Redis", e);
            throw new NoticeException.RedisOperationFailedException();
        }
    }

    private void saveNoticeReceive(UUID memberId, NoticeSend noticeSend, String content) {
        NoticeReceive noticeReceive = NoticeReceive.builder()
                .noticeSendId(noticeSend.getId())
                .noticeContent(content)
                .noticeTitle(noticeSend.getNotice().getNoticeTitle())
                .noticeType(noticeSend.getNotice().getNoticeType())
                .build();

        noticeReceiveRepository.save(noticeReceive);
    }
}