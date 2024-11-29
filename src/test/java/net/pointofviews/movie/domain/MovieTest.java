package net.pointofviews.movie.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import net.pointofviews.review.domain.Review;

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
                String country = "USA";
                LocalDateTime released = LocalDateTime.of(2010, 7, 16, 0, 0);
                String imdbId = "tt1375666";
                boolean hasAward = true;

                // when
                Movie movie = Movie.builder()
                        .title(title)
                        .director(director)
                        .writer(writer)
                        .plot(plot)
                        .poster(poster)
                        .country(country)
                        .released(released)
                        .imdbId(imdbId)
                        .hasAward(hasAward)
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
                    softly.assertThat(movie.getImdbId()).isEqualTo(imdbId);
                    softly.assertThat(movie.isHasAward()).isTrue();
                });

            }
        }

        @Nested
        class Failure {

            @Test
            void 제목_없음_IllegalArgumentException_예외발생() {
                // when & then
                assertThatThrownBy(() -> {
                    Movie.builder()
                            .title(null) // 제목이 null인 경우
                            .director("Christopher Nolan")
                            .writer("Jonathan Nolan")
                            .plot("A mind-bending thriller.")
                            .poster("poster.jpg")
                            .country("USA")
                            .released(LocalDateTime.of(2010, 7, 16, 0, 0))
                            .imdbId("tt1375666")
                            .hasAward(true)
                            .build();
                }).isInstanceOf(IllegalArgumentException.class)
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
                    .released(LocalDateTime.now())
                    .imdbId("tt1375666")
                    .hasAward(true)
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