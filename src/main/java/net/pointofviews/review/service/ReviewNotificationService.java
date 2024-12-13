package net.pointofviews.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieGenre;
import net.pointofviews.notice.dto.request.SendNoticeRequest;
import net.pointofviews.notice.service.NoticeService;
import net.pointofviews.review.domain.Review;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewNotificationService {

    private final NoticeService noticeService;
    private final CommonCodeService commonCodeService;
    private static final Long REVIEW_NOTICE_TEMPLATE_ID = 1L;  // 알림 템플릿 ID

    @Transactional
    public void sendReviewNotifications(Review review) {
        Movie movie = review.getMovie();
        for (MovieGenre movieGenre : movie.getGenres()) {
            String genreName = commonCodeService.convertCommonCodeToName(
                    movieGenre.getGenreCode(),
                    CodeGroupEnum.MOVIE_GENRE
            );

            String noticeContent = String.format("%s 장르의 '%s'에 새로운 리뷰가 작성되었습니다.",
                    genreName, movie.getTitle());

            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("genre", genreName);
            templateVariables.put("movieTitle", movie.getTitle());
            templateVariables.put("review_id", String.valueOf(review.getId()));
            templateVariables.put("notice_content", noticeContent);

            SendNoticeRequest noticeRequest = new SendNoticeRequest(
                    REVIEW_NOTICE_TEMPLATE_ID,
                    templateVariables
            );

            noticeService.sendNotice(noticeRequest);
        }
    }
}
