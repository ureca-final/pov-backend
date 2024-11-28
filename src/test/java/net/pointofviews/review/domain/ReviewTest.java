package net.pointofviews.review.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.pointofviews.movie.domain.Movie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.pointofviews.member.domain.Member;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class ReviewTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void Review_객체_생성() {
                // given
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);
                String title = "리뷰 제목";
                String contents = "리뷰 내용";
                boolean isSpoiler = true;
                String preference = "긍정적";

                BDDMockito.given(movie.getReviews()).willReturn(new ArrayList<>());

                // when
                Review review = Review.builder()
                        .member(member)
                        .movie(movie)
                        .title(title)
                        .contents(contents)
                        .isSpoiler(isSpoiler)
                        .preference(preference)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(review).isNotNull();
                    softly.assertThat(review.getMember()).isEqualTo(member);
                    softly.assertThat(review.getMovie()).isEqualTo(movie);
                    softly.assertThat(review.getTitle()).isEqualTo(title);
                    softly.assertThat(review.getContents()).isEqualTo(contents);
                    softly.assertThat(review.isSpoiler()).isEqualTo(isSpoiler);
                    softly.assertThat(review.getPreference()).isEqualTo(preference);
                    softly.assertThat(review.isDisabled()).isFalse();
                    softly.assertThat(review.getModifiedAt()).isNull();
                });
            }
        }

        @Nested
        class failure {

            @Test
            void 제목_없음_IllegalArgumentException_예외발생() {
                // given
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);
                String contents = "리뷰 내용";
                boolean isSpoiler = false;
                String preference = "긍정적";

                // when & then
                assertThatThrownBy(() -> Review.builder()
                        .member(member)
                        .movie(movie)
                        .contents(contents)
                        .isSpoiler(isSpoiler)
                        .preference(preference)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}