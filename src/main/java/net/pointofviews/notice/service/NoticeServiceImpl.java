package net.pointofviews.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.common.service.RedisService;
import net.pointofviews.member.domain.MemberFcmToken;
import net.pointofviews.member.repository.MemberFcmTokenRepository;
import net.pointofviews.movie.domain.MovieGenre;
import net.pointofviews.notice.domain.*;
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
    private final RedisService stringRedisService;

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
        // 리뷰 조회
        Long reviewId = parseIdOrNull(request.templateVariables().get("review_id"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(NoticeException.ReviewNotFoundException::new);

        // 알림 대상자 조회
        Set<UUID> targetMembers = getTargetMembers(review);
        final UUID reviewAuthorId = review.getMember().getId();

        // 리뷰 작성자 제외
        if (reviewAuthorId != null) {
            targetMembers = targetMembers.stream()
                    .filter(memberId -> !memberId.equals(reviewAuthorId))
                    .collect(Collectors.toSet());
        }

        if (targetMembers.isEmpty()) {
            log.info("모든 장르에 대해 알림을 받을 대상자가 없습니다.");
            return;
        }

        // 알림 템플릿 및 내용 생성
        Notice noticeTemplate = noticeRepository.findByIdAndIsActiveTrue(request.noticeTemplateId())
                .orElseThrow(NoticeException.NoticeTemplateNotFoundException::new);

        String content = replaceTemplateVariables(noticeTemplate.getNoticeContent(), request.templateVariables());

        // NoticeSend 생성 (알림 발송 이력 기록)
        NoticeSend noticeSend = NoticeSend.builder()
                .notice(noticeTemplate)
                .noticeContentDetail(content)
                .isSucceed(true)
                .build();
        noticeSendRepository.save(noticeSend);

        // 배치 처리
        List<UUID> memberList = new ArrayList<>(targetMembers);
        Map<String, MemberFcmToken> tokenMap = new HashMap<>();  // token과 MemberFcmToken 매핑 저장
        int totalSentCount = 0;

        for (int i = 0; i < memberList.size(); i += BATCH_SIZE) {
            int end = Math.min(memberList.size(), i + BATCH_SIZE);
            List<UUID> batchMembers = memberList.subList(i, end);

            // 활성 상태인 fcm 토큰 조회
            List<MemberFcmToken> fcmTokens = memberFcmTokenRepository.findActiveTokensByMemberIds(batchMembers);
            if (fcmTokens.isEmpty()) {
                continue;
            }

            // 토큰 매핑 및 알림 수신 객체 생성
            List<String> tokenList = new ArrayList<>();
            for (MemberFcmToken fcmToken : fcmTokens) {
                tokenMap.put(fcmToken.getFcmToken(), fcmToken);
                tokenList.add(fcmToken.getFcmToken());
            }

            try {
                // fcm 메세지 발송
                String noticeContent = request.templateVariables().getOrDefault("notice_content", content);
                List<FcmResult> fcmResults = fcmUtil.sendMessage(
                        tokenList,
                        noticeTemplate.getNoticeTitle(),
                        content,
                        reviewId,
                        noticeContent,
                        noticeSend
                );

                // 발송 결과 처리
                List<NoticeReceive> successNoticeReceives = new ArrayList<>();
                for (FcmResult result : fcmResults) {
                    if (result.isSuccess()) {
                        MemberFcmToken fcmToken = tokenMap.get(result.getToken());
                        if (fcmToken != null) {
                            NoticeReceive noticeReceive = NoticeReceive.builder()
                                    .member(fcmToken.getMember())
                                    .noticeSendId(noticeSend.getId())
                                    .noticeContent(content)
                                    .noticeTitle(noticeTemplate.getNoticeTitle())
                                    .noticeType(noticeTemplate.getNoticeType())
                                    .reviewId(reviewId)
                                    .build();
                            successNoticeReceives.add(noticeReceive);
                            totalSentCount++;
                        }
                    } else if (result.isInvalidToken()) {
                        memberFcmTokenRepository.deactivateToken(result.getToken());
                    }
                }

                if (!successNoticeReceives.isEmpty()) {
                    noticeReceiveRepository.saveAll(successNoticeReceives);
                }
            } catch (Exception e) {
                log.error("Failed to process notices", e);
                noticeSend.setSucceed(false);
                noticeSendRepository.save(noticeSend);
                throw new NoticeException.NoticeReceiveSaveFailedException();
            }
        }

        // 전체 발송 결과 업데이트
        if (totalSentCount == 0 && !memberList.isEmpty()) {
            noticeSend.setSucceed(false);
            noticeSendRepository.save(noticeSend);
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

    // 알림 대상자 수집 로직 분리
    private Set<UUID> getTargetMembers(Review review) {
        Set<UUID> targetMembers = new HashSet<>();

        for (MovieGenre movieGenre : review.getMovie().getGenres()) {
            String genreName = commonCodeService.convertCommonCodeToName(
                    movieGenre.getGenreCode(),
                    CodeGroupEnum.MOVIE_GENRE
            );
            String genreKey = generateRedisKey(movieGenre.getGenreCode());
            Set<UUID> genreTargetMembers = getTargetMembers(genreKey);

            if (genreTargetMembers.isEmpty()) {
                log.info("영화장르(장르명: {}, 장르코드: {})에 대한 알림을 받을 대상자가 없습니다.",
                        genreName, movieGenre.getGenreCode());
            } else {
                log.info("영화장르(장르명: {}, 장르코드: {})에 대한 알림 대상자 수: {}",
                        genreName, movieGenre.getGenreCode(), genreTargetMembers.size());
            }

            targetMembers.addAll(genreTargetMembers);
        }

        return targetMembers;
    }

    // redis에서 실제 대상자 uuid 조회
    private Set<UUID> getTargetMembers(String genreKey) {
        try {
            Set<String> members = stringRedisService.getKeys(genreKey);
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