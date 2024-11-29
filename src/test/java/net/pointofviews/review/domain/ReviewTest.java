package net.pointofviews.review.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.Movie;

@ExtendWith(MockitoExtension.class)
class ReviewTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Review_객체_생성() {
                // given
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);
                String title = "리뷰 제목";
                String contents = "리뷰 내용";
                boolean isSpoiler = true;
                String preference = "긍정적";

                given(movie.getReviews()).willReturn(new ArrayList<>());

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
                assertSoftly(softly -> {
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
        class Failure {

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

    @Nested
    class ToggleDisabled {

        @Nested
        class Success {
            @Test
            void 리뷰_숨김_상태_변화_False_to_True() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);

                Review review = Review.builder()
                    .member(member)
                    .movie(movie)
                    .title("리뷰 제목")
                    .contents("리뷰 내용")
                    .preference("긍정적")
                    .isSpoiler(false)
                    .build();

                // when -- 테스트하고자 하는 행동
                review.toggleDisabled();

                // then -- 예상되는 변화 및 결과
                assertThat(review.isDisabled()).isTrue();
            }

            @Test
            void 리뷰_숨김_상태_변화_True_to_False() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);

                Review review = Review.builder()
                    .member(member)
                    .movie(movie)
                    .title("리뷰 제목")
                    .contents("리뷰 내용")
                    .preference("긍정적")
                    .isSpoiler(false)
                    .build();

                // when -- 테스트하고자 하는 행동
                review.toggleDisabled();
                review.toggleDisabled();

                // then -- 예상되는 변화 및 결과
                assertThat(review.isDisabled()).isFalse();
            }
        }
    }
}