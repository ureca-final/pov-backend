package net.pointofviews.movie.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AwardTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void Award_객체_생성() {
                // given
                Movie movie = Mockito.mock(Movie.class);
                String awardName = "Best Picture";

                BDDMockito.given(movie.getId()).willReturn(1L);
                BDDMockito.given(movie.getTitle()).willReturn("Inception");

                // when
                Award award = Award.builder()
                        .name(awardName)
                        .movie(movie)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(award).isNotNull();
                    softly.assertThat(award.getName()).isEqualTo(awardName);
                    softly.assertThat(award.getMovie()).isNotNull(); // Movie가 설정되었는지 확인
                    softly.assertThat(award.getMovie().getId()).isEqualTo(1L);
                    softly.assertThat(award.getMovie().getTitle()).isEqualTo("Inception");
                });

            }
        }
    }
}