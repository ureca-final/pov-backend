package net.pointofviews.movie.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieContentTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void MovieContent_객체_생성() {
                // given
                Movie movie = Mockito.mock(Movie.class);
                BDDMockito.given(movie.getId()).willReturn(1L);

                String content = "This is a movie review.";
                MovieContentType contentType = MovieContentType.YOUTUBE;

                // when
                MovieContent movieContent = MovieContent.builder()
                        .movie(movie)
                        .content(content)
                        .contentType(contentType)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(movieContent).isNotNull();
                    softly.assertThat(movieContent.getMovie()).isNotNull();
                    softly.assertThat(movieContent.getMovie().getId()).isEqualTo(1L);
                    softly.assertThat(movieContent.getContent()).isEqualTo(content);
                    softly.assertThat(movieContent.getContentType()).isEqualTo(contentType);
                });

            }
        }
    }
}