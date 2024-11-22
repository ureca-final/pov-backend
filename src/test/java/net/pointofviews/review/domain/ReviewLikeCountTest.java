package net.pointofviews.review.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReviewLikeCountTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void ReviewLikeCount_객체_생성() {
                // given
                Review review = mock(Review.class);

                // when
                ReviewLikeCount reviewLikeCount = ReviewLikeCount.builder()
                        .review(review)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(reviewLikeCount).isNotNull();
                    softly.assertThat(reviewLikeCount.getReview()).isEqualTo(review);
                    softly.assertThat(reviewLikeCount.getReviewLikeCount()).isEqualTo(0L);
                });
            }
        }
    }
}