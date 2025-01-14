package net.pointofviews.movie.domain;

import net.pointofviews.people.domain.People;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
            void People_객체_생성() {
                // given
                Movie movie = Mockito.mock(Movie.class);
                String name = "Leonardo DiCaprio";
                String imageUrl = "imageUrl";
                Integer tmdbId = 124;

                // when
                People people = People.builder()
                        .name(name)
                        .imageUrl(imageUrl)
                        .tmdbId(tmdbId)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(people).isNotNull();
                    softly.assertThat(people.getName()).isEqualTo(name);
                    softly.assertThat(people.getImageUrl()).isEqualTo(imageUrl);
                    softly.assertThat(people.getTmdbId()).isEqualTo(tmdbId);
                });

            }
        }

        @Nested
        class Failure {

            @Test
            void 이름_없음_IllegalArgumentException_예외발생() {
                // given
                String imageUrl = "imageUrl";
                Integer tmdbId = 124;

                // when & then
                assertThatThrownBy(() -> {
                    People.builder()
                            .name(null)
                            .imageUrl(imageUrl)
                            .tmdbId(tmdbId)
                            .build(); // 이름이 null인 경우
                }).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("name must not be null");
            }
        }
    }
}