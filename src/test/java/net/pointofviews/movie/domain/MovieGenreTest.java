package net.pointofviews.movie.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MovieGenreTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void MovieGenre_객체_생성() {
                // given
                Movie movie = Mockito.mock(Movie.class);
                BDDMockito.given(movie.getId()).willReturn(1L);

                String genreCode = "01"; // Genre Code는 항상 2자리

                // when
                MovieGenre movieGenre = MovieGenre.builder()
                        .movie(movie)
                        .genreCode(genreCode)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(movieGenre).isNotNull();
                    softly.assertThat(movieGenre.getMovie()).isNotNull();
                    softly.assertThat(movieGenre.getMovie().getId()).isEqualTo(1L);
                    softly.assertThat(movieGenre.getGenreCode()).isEqualTo(genreCode);
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void GenreCode_없음_IllegalArgumentException_예외발생() {
                // when & then
                assertThatThrownBy(() -> MovieGenre.builder()
                        .movie(Mockito.mock(Movie.class))
                        .genreCode(null) // GenreCode가 null인 경우
                        .build()).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("GenreCode must be exactly 2 characters");
            }

            @Test
            void GenreCode_길이_초과_IllegalArgumentException_예외발생() {
                // when & then
                assertThatThrownBy(() -> MovieGenre.builder()
                        .movie(Mockito.mock(Movie.class))
                        .genreCode("010") // GenreCode가 2자리를 초과하는 경우
                        .build()).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("GenreCode must be exactly 2 characters");
            }
        }
    }
}