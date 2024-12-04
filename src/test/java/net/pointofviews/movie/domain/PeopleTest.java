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
class PeopleTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void Actor_객체_생성() {
                // given
                Movie movie = Mockito.mock(Movie.class);
                String name = "Leonardo DiCaprio";

                BDDMockito.given(movie.getId()).willReturn(1L);
                BDDMockito.given(movie.getTitle()).willReturn("Inception");

                // when
                People people = People.builder()
                        .name(name)
                        .movie(movie)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(people).isNotNull();
                    softly.assertThat(people.getName()).isEqualTo(name);
                    softly.assertThat(people.getMovie()).isNotNull(); // Movie가 설정되었는지 확인
                    softly.assertThat(people.getMovie().getId()).isEqualTo(1L);
                    softly.assertThat(people.getMovie().getTitle()).isEqualTo("Inception");
                });

            }
        }

        @Nested
        class Failure {

            @Test
            void 이름_없음_IllegalArgumentException_예외발생() {
                // when & then
                assertThatThrownBy(() -> {
                    People.builder()
                            .name(null)
                            .movie(Mockito.mock(Movie.class))
                            .build(); // 이름이 null인 경우
                }).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("name must not be null");
            }
        }
    }
}