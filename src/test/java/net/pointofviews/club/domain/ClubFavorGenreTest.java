package net.pointofviews.club.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ClubFavorGenreTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void ClubFavorGenre_객체_생성() {
                // given
                String genreCode = "00";
                Club club = mock(Club.class);

                // when
                ClubFavorGenre genre = ClubFavorGenre.builder()
                        .club(club)
                        .genreCode(genreCode)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(genre.getGenreCode()).isEqualTo(genreCode);
                    softly.assertThat(genre.getClub()).isEqualTo(club);
                });
            }
        }
    }
}