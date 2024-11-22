package net.pointofviews.review.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReviewKeywordLinkTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void ReviewKeywordLink_객체_생성() {
                // given
                Review review = mock(Review.class);
                String reviewKeywordCode = "01";

                // when
                ReviewKeywordLink keywordLink = ReviewKeywordLink.builder()
                        .review(review)
                        .reviewKeywordCode(reviewKeywordCode)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(keywordLink).isNotNull();
                    softly.assertThat(keywordLink.getReview()).isEqualTo(review);
                    softly.assertThat(keywordLink.getReviewKeywordCode()).isEqualTo(reviewKeywordCode);
                });
            }
        }
    }
}