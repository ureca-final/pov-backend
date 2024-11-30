package net.pointofviews.review.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReviewLikeTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void ReviewLike_객체_생성() {
                // given
                Member member = mock(Member.class);
                Review review = mock(Review.class);

                // when
                ReviewLike reviewLike = ReviewLike.builder()
                        .member(member)
                        .review(review)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(reviewLike).isNotNull();
                    softly.assertThat(reviewLike.getMember()).isEqualTo(member);
                    softly.assertThat(reviewLike.getReview()).isEqualTo(review);
                    softly.assertThat(reviewLike.getCreatedAt()).isNull();
                });
            }
        }
    }
}