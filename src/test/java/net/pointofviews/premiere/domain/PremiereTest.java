package net.pointofviews.premiere.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class PremiereTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Premiere_객체_생성() {
                // given
                String title = "premiere title";
                String eventImage = "image.jpg";
                boolean isPaymentRequired = true;
                LocalDateTime startAt = LocalDateTime.of(2024, 11, 22, 14, 30);
                LocalDateTime endAt = LocalDateTime.of(2024, 11, 28, 14, 30);

                // when
                Premiere premiere = Premiere.builder()
                        .title(title)
                        .eventImage(eventImage)
                        .isPaymentRequired(isPaymentRequired)
                        .startAt(startAt)
                        .endAt(endAt)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(premiere).isNotNull();
                    softly.assertThat(premiere.getTitle()).isEqualTo(title);
                    softly.assertThat(premiere.getEventImage()).isEqualTo(eventImage);
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(isPaymentRequired);
                    softly.assertThat(premiere.getStartAt()).isEqualTo(startAt);
                });
            }
        }
    }
}