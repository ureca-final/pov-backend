package net.pointofviews.movie.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                BDDMockito.given(movie.getId()).willReturn(1L);

                Long likeCount = 10L;

                // when
                MovieLikeCount movieLikeCount = MovieLikeCount.builder()
                        .movie(movie)
                        .likeCount(likeCount)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(movieLikeCount).isNotNull();
                    softly.assertThat(movieLikeCount.getMovie()).isNotNull();
                    softly.assertThat(movieLikeCount.getMovieId()).isEqualTo(1L);
                    softly.assertThat(movieLikeCount.getLikeCount()).isEqualTo(likeCount);
                });
            }
        }
    }
}