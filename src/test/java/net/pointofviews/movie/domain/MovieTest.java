package net.pointofviews.movie.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MovieTest {

    @Nested
    class Constructor {

        @Nested
        class success {

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
                SoftAssertions.assertSoftly(softly -> {
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
        class failure {

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
}