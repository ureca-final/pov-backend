package net.pointofviews.club.domain;

import net.pointofviews.movie.domain.Movie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ClubMovieTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void ClubMovie_객체_생성() {
                // given
                Club club = mock(Club.class);
                Movie movie = mock(Movie.class);

                // when
                ClubMovie clubMovie = ClubMovie.builder()
                        .club(club)
                        .movie(movie)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(clubMovie.getClub()).isEqualTo(club);
                    softly.assertThat(clubMovie.getMovie()).isEqualTo(movie);
                });
            }
        }
    }

}