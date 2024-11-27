package net.pointofviews.movie.domain;

import static org.mockito.BDDMockito.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieLikeCountTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void MovieLikeCount_객체_생성() {
                // given
                Movie movie = Mockito.mock(Movie.class);

                given(movie.getId()).willReturn(1L);

                // when
                MovieLikeCount movieLikeCount = MovieLikeCount.builder()
                        .movie(movie)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(movieLikeCount).isNotNull();
                    softly.assertThat(movieLikeCount.getMovie()).isNotNull();
                    softly.assertThat(movieLikeCount.getMovie().getId()).isEqualTo(1L);
                    softly.assertThat(movieLikeCount.getLikeCount()).isEqualTo(0L);
                });
            }
        }
    }
}