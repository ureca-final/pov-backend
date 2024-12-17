package net.pointofviews.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.notice.dto.request.SendNoticeRequest;
import net.pointofviews.notice.service.NoticeService;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.service.ReviewNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewNotificationServiceImpl implements ReviewNotificationService {

    private final NoticeService noticeService;
    private final CommonCodeService commonCodeService;
    private static final Long REVIEW_NOTICE_TEMPLATE_ID = 1L;  // 알림 템플릿 ID

    @Override
    @Transactional
    public void sendReviewNotifications(Review review) {
        Movie movie = review.getMovie();

        // 모든 장르 이름을 하나의 문자열로 결합
        String allGenres = movie.getGenres().stream()
                .map(movieGenre -> commonCodeService.convertCommonCodeToName(
                        movieGenre.getGenreCode(),
                        CodeGroupEnum.MOVIE_GENRE
                ))
                .collect(Collectors.joining(", "));

        String noticeContent = String.format("%s 장르의 '%s'에 새로운 리뷰가 작성되었습니다.",
                allGenres, movie.getTitle());

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("genre", allGenres);
        templateVariables.put("movieTitle", movie.getTitle());
        templateVariables.put("review_id", String.valueOf(review.getId()));
        templateVariables.put("notice_content", noticeContent);

        SendNoticeRequest noticeRequest = new SendNoticeRequest(
                REVIEW_NOTICE_TEMPLATE_ID,
                templateVariables
        );

        log.info("Send notice request: {}", noticeRequest);
        noticeService.sendNotice(noticeRequest);
    }
}
