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
        class success {

            @Test
            void Premiere_객체_생성() {
                // given
                String title = "premiere title";
                String content = "premiere content";
                String eventImage = "image.jpg";
                boolean isPaymentRequired = true;
                LocalDateTime startAt = LocalDateTime.now().plusDays(1);

                // when
                Premiere premiere = Premiere.builder()
                        .title(title)
                        .content(content)
                        .eventImage(eventImage)
                        .isPaymentRequired(isPaymentRequired)
                        .startAt(startAt)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(premiere).isNotNull();
                    softly.assertThat(premiere.getTitle()).isEqualTo(title);
                    softly.assertThat(premiere.getContent()).isEqualTo(content);
                    softly.assertThat(premiere.getEventImage()).isEqualTo(eventImage);
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(isPaymentRequired);
                    softly.assertThat(premiere.getStartAt()).isEqualTo(startAt);
                });
            }
        }
    }
}