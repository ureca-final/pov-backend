package net.pointofviews.movie.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MovieLikeTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void MovieLike_객체_생성() {
                // given
                Member member = Mockito.mock(Member.class);
                Movie movie = Mockito.mock(Movie.class);

                UUID memberId = UUID.randomUUID();
                BDDMockito.given(member.getId()).willReturn(memberId);
                BDDMockito.given(movie.getId()).willReturn(1L);

                // when
                MovieLike movieLike = MovieLike.builder()
                        .member(member)
                        .movie(movie)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(movieLike).isNotNull();
                    softly.assertThat(movieLike.getMember()).isNotNull();
                    softly.assertThat(movieLike.getMember().getId()).isEqualTo(memberId);
                    softly.assertThat(movieLike.getMovie()).isNotNull();
                    softly.assertThat(movieLike.getMovie().getId()).isEqualTo(1L);
                });
            }
        }
    }
}