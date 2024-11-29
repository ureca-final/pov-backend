package net.pointofviews.movie.domain;

import net.pointofviews.review.domain.Review;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MovieTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Movie_객체_생성() {
                // given
                String title = "Inception";
                String director = "Christopher Nolan";
                String writer = "Jonathan Nolan";
                String plot = "A mind-bending thriller about dreams within dreams.";
                String poster = "inception-poster.jpg";
                String backdrop = "inception-backdrop.jpg";
                String country = "USA";
                LocalDate released = LocalDate.of(2010, 7, 16);
                Integer tmdbId = 375666;
                boolean hasAward = true;
                boolean isAdult = false;

                // when
                Movie movie = Movie.builder()
                        .title(title)
                        .director(director)
                        .writer(writer)
                        .plot(plot)
                        .poster(poster)
                        .country(country)
                        .released(released)
                        .tmdbId(tmdbId)
                        .hasAward(hasAward)
                        .backdrop(backdrop)
                        .isAdult(isAdult)
                        .build();

                // then
                assertSoftly(softly -> {
                    softly.assertThat(movie).isNotNull();
                    softly.assertThat(movie.getTitle()).isEqualTo(title);
                    softly.assertThat(movie.getDirector()).isEqualTo(director);
                    softly.assertThat(movie.getWriter()).isEqualTo(writer);
                    softly.assertThat(movie.getPlot()).isEqualTo(plot);
                    softly.assertThat(movie.getPoster()).isEqualTo(poster);
                    softly.assertThat(movie.getCountry()).isEqualTo(country);
                    softly.assertThat(movie.getReleased()).isEqualTo(released);
                    softly.assertThat(movie.getTmdbId()).isEqualTo(tmdbId);
                    softly.assertThat(movie.isHasAward()).isTrue();
                    softly.assertThat(movie.isAdult()).isEqualTo(isAdult);
                    softly.assertThat(movie.getBackdrop()).isEqualTo(backdrop);
                });

            }
        }

        @Nested
        class Failure {

            @Test
            void 제목_없음_IllegalArgumentException_예외발생() {
                // when & then
                assertThatThrownBy(() -> Movie.builder()
                        .title(null) // 제목이 null인 경우
                        .director("Christopher Nolan")
                        .writer("Jonathan Nolan")
                        .plot("A mind-bending thriller.")
                        .poster("poster.jpg")
                        .country("USA")
                        .released(LocalDate.of(2010, 7, 16))
                        .tmdbId(375666)
                        .hasAward(true)
                        .build()).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("title must not be null");
            }

        }
    }

    @Nested
    class AddReview {

        @Nested
        class Success {
            @Test
            void 영화_리뷰_추가() {
                // given -- 테스트의 상태 설정
                Movie movie = Movie.builder()
                        .title("Inception")
                        .director("Christopher Nolan")
                        .writer("Jonathan Nolan")
                        .plot("A mind-bending thriller about dreams within dreams.")
                        .poster("inception-poster.jpg")
                        .country("USA")
                        .released(LocalDate.now())
                        .tmdbId(1375666)
                        .hasAward(true)
                        .isAdult(false)
                        .build();

                Review review = mock(Review.class);

                // when -- 테스트하고자 하는 행동
                movie.addReview(review);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(movie.getReviews()).contains(review);
                    softly.assertThat(movie.getReviews().size()).isEqualTo(1);
                });
            }
        }
    }
}