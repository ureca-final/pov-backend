package net.pointofviews.club.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ClubTest {
    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Club_객체_생성() {
                // given
                String name = "club name";
                String description = "club description";
                Integer maxParticipants = 10;
                boolean isPublic = true;

                // when
                Club club = Club.builder()
                        .name(name)
                        .description(description)
                        .maxParticipants(maxParticipants)
                        .isPublic(isPublic)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(club).isNotNull();
                    softly.assertThat(club.getName()).isEqualTo(name);
                    softly.assertThat(club.getDescription()).isEqualTo(description);
                    softly.assertThat(club.getMaxParticipants()).isEqualTo(maxParticipants);
                    softly.assertThat(club.isPublic()).isEqualTo(isPublic);
                    softly.assertThat(club.getId()).isNull();
                });
            }

            @ParameterizedTest
            @MethodSource("provideInvalidMaxParticipants")
            void Club_객체_생성_maxParticipants_범위_초과시_기본값_설정(Integer value) {
                // given
                String name = "club name";
                String description = "club description";
                boolean isPublic = true;

                // when
                Club club = Club.builder()
                        .name(name)
                        .description(description)
                        .maxParticipants(value)
                        .isPublic(isPublic)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(club).isNotNull();
                    softly.assertThat(club.getName()).isEqualTo(name);
                    softly.assertThat(club.getDescription()).isEqualTo(description);
                    softly.assertThat(club.getMaxParticipants()).isEqualTo(1000);
                    softly.assertThat(club.isPublic()).isEqualTo(isPublic);
                    softly.assertThat(club.getId()).isNull();
                });
            }

            private static Stream<Arguments> provideInvalidMaxParticipants() {
                return Stream.of(
                        null,
                        Arguments.of(0),
                        Arguments.of(-1),
                        Arguments.of(1001)
                );
            }
        }
    }
}