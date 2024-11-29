package net.pointofviews.movie.domain;

import net.pointofviews.member.domain.Member;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(MockitoExtension.class)
class RecommendedMovieTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void RecommendedMovie_객체_생성() {
                // given
                Member member = Mockito.mock(Member.class);
                Movie movie = Mockito.mock(Movie.class);

                UUID memberId = UUID.randomUUID();
                BDDMockito.given(member.getId()).willReturn(memberId);
                BDDMockito.given(movie.getId()).willReturn(1L);

                // when
                RecommendedMovie recommendedMovie = RecommendedMovie.builder()
                        .member(member)
                        .movie(movie)
                        .build();

                // then
                assertSoftly(softly -> {
                    softly.assertThat(recommendedMovie).isNotNull();
                    softly.assertThat(recommendedMovie.getMember()).isNotNull();
                    softly.assertThat(recommendedMovie.getMember().getId()).isEqualTo(memberId);
                    softly.assertThat(recommendedMovie.getMovie()).isNotNull();
                    softly.assertThat(recommendedMovie.getMovie().getId()).isEqualTo(1L);
                });
            }
        }
    }
}