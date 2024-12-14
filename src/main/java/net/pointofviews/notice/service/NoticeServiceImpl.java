package net.pointofviews.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.member.domain.MemberFcmToken;
import net.pointofviews.member.repository.MemberFcmTokenRepository;
import net.pointofviews.movie.domain.MovieGenre;
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
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.repository.ReviewRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    private static final int BATCH_SIZE = 500;

    private final NoticeRepository noticeRepository;
    private final NoticeSendRepository noticeSendRepository;
    private final NoticeReceiveRepository noticeReceiveRepository;
    private final MemberFcmTokenRepository memberFcmTokenRepository;
    private final ReviewRepository reviewRepository;
    private final CommonCodeService commonCodeService;
    private final FcmUtil fcmUtil;
    private final StringRedisTemplate stringRedisTemplate;

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
        // 리뷰 작성자 ID 가져오기
        Long reviewId = parseIdOrNull(request.templateVariables().get("review_id"));

        if(reviewId == null){
            log.error("리뷰가 존재하지 않아 알림 전송이 불가능합니다.");
            return;
        }

        // 리뷰의 영화에 있는 모든 장르의 선호 사용자 조회
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        Review review = reviewOptional.get();
        Set<UUID> targetMembers = new HashSet<>();

        // 각 장르별 선호 사용자 조회 및 통합 (중복 제거)
        for (MovieGenre movieGenre : review.getMovie().getGenres()) {
            String genreName = commonCodeService.convertCommonCodeToName(
                    movieGenre.getGenreCode(),
                    CodeGroupEnum.MOVIE_GENRE
            );
            String genreKey = generateRedisKey(movieGenre.getGenreCode());
            Set<UUID> genreTargetMembers = getTargetMembers(genreKey);

            String message;
            if (genreTargetMembers.isEmpty()) {
                message = String.format("영화장르(장르명: %s, 장르코드: %s)에 대한 알림을 받을 대상자가 없습니다.",
                        genreName, movieGenre.getGenreCode());
            } else {
                message = String.format("영화장르(장르명: %s, 장르코드: %s)에 대한 알림 대상자 수: %d",
                        genreName, movieGenre.getGenreCode(), genreTargetMembers.size());
            }
            log.info(message);

            targetMembers.addAll(genreTargetMembers);
        }

        // 리뷰 작성자 제외
        final UUID reviewAuthorId = review.getMember().getId();
        if (reviewAuthorId != null) {
            targetMembers = targetMembers.stream()
                    .filter(memberId -> !memberId.equals(reviewAuthorId))
                    .collect(Collectors.toSet());
        }

        if (targetMembers.isEmpty()) {
            log.info("모든 장르에 대해 알림을 받을 대상자가 없습니다.");
            return;
        }

        Notice noticeTemplate = noticeRepository.findByIdAndIsActiveTrue(request.noticeTemplateId())
                .orElseThrow(NoticeException.NoticeTemplateNotFoundException::new);

        String content = replaceTemplateVariables(noticeTemplate.getNoticeContent(), request.templateVariables());

        NoticeSend noticeSend = NoticeSend.builder()
                .notice(noticeTemplate)
                .noticeContentDetail(content)
                .isSucceed(true)
                .build();

        noticeSendRepository.save(noticeSend);

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
            List<String> tokenList = new ArrayList<>();


            // 알림 수신 객체 생성 및 토큰 리스트 생성
            for (MemberFcmToken fcmToken : fcmTokens) {
                NoticeReceive noticeReceive = NoticeReceive.builder()
                        .member(fcmToken.getMember())
                        .noticeSendId(noticeSend.getId())
                        .noticeContent(content)
                        .noticeTitle(noticeTemplate.getNoticeTitle())
                        .noticeType(noticeTemplate.getNoticeType())
                        .reviewId(reviewId)
                        .build();

                noticeReceives.add(noticeReceive);
                tokenList.add(fcmToken.getFcmToken());
            }

            try {
                String noticeContent = request.templateVariables().getOrDefault("notice_content", content);

                fcmUtil.sendMessage(
                        tokenList,
                        noticeTemplate.getNoticeTitle(),
                        content,
                        reviewId,
                        noticeContent
                );

                if (!noticeReceives.isEmpty()) {
                    noticeReceiveRepository.saveAll(noticeReceives);
                }
            } catch (NoticeException.NoticeSendFailedException e) {
                log.error("Failed to send batch notifications");
                noticeSend.setSucceed(false);
                noticeSendRepository.save(noticeSend);

            } catch (Exception e) {
                log.error("Failed to save notice receives", e);
                noticeSend.setSucceed(false);
                throw new NoticeException.NoticeReceiveSaveFailedException();
            }
        }
    }

    @Override
    public List<ReadNoticeResponse> findNotices(UUID memberId) {
        return noticeReceiveRepository.findByMemberIdWithReviewAndMovieOrderByCreatedAtDesc(memberId)
                .stream()
                .map(receive -> new ReadNoticeResponse(
                        receive.getId(),
                        receive.getNoticeTitle(),
                        receive.getNoticeContent(),
                        receive.getNoticeType(),
                        receive.isRead(),
                        receive.getCreatedAt(),
                        receive.getReviewId(),
                        receive.getReviewId() != null ? reviewRepository.findById(receive.getReviewId())
                                .map(review -> review.getMovie().getId())
                                .orElse(null) : null
                ))
                .toList();
    }

    @Override
    @Transactional
    public void updateNotice(UUID memberId, Long noticeId) {
        NoticeReceive noticeReceive = noticeReceiveRepository.findByIdAndMemberId(noticeId, memberId)
                .orElseThrow(NoticeException.NoticeNotFoundException::new);

        // 알림 확인시, 알림 내역 삭제
        noticeReceiveRepository.delete(noticeReceive);
    }

    private String replaceTemplateVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                result = result.replace("{" + entry.getKey() + "}", value);
            } else {
                result = result.replace("{" + entry.getKey() + "}", "");  // null인 경우 빈 문자열로 대체
            }
        }
        return result;
    }

    private Set<UUID> getTargetMembers(String genreKey) {
        try {
            Set<String> members = stringRedisTemplate.opsForSet().members(genreKey);
            if (members == null) {
                return new HashSet<>();
            }

            return members.stream()
                    .map(member -> {
                        try {
                            return UUID.fromString(member);
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid UUID string: {}", member);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Failed to get target members from Redis: {}", e.getMessage(), e);
            throw new NoticeException.RedisOperationFailedException();
        }
    }

    private Long parseIdOrNull(String idStr) {
        try {
            return !idStr.isEmpty() ? Long.parseLong(idStr) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String generateRedisKey(String genreCode) {
        return "genre:preferences:" + genreCode;
    }
}