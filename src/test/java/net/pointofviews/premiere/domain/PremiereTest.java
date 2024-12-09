package net.pointofviews.premiere.domain;

import net.pointofviews.premiere.dto.request.PremiereRequest;
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
                    softly.assertThat(premiere.getEndAt()).isEqualTo(endAt);
                });
            }
        }
    }

    @Nested
    class UpdatePremiere {

        @Nested
        class Success {

            @Test
            void 시사회_정보_수정() {
                // given -- 테스트의 상태 설정
                LocalDateTime startAt = LocalDateTime.of(2024, 11, 22, 14, 30);
                LocalDateTime endAt = LocalDateTime.of(2024, 11, 28, 14, 30);
                
                Premiere premiere = Premiere.builder()
                        .title("title")
                        .eventImage("https://example.com/images/premiere.jpg")
                        .isPaymentRequired(true)
                        .startAt(startAt)
                        .endAt(endAt)
                        .build();

                PremiereRequest request = new PremiereRequest(
                        "Update Premiere Title",
                        "https://example.com/images/update-premiere.jpg",
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        LocalDateTime.of(2024, 12, 22, 14, 30),
                        true
                );

                // when -- 테스트하고자 하는 행동
                premiere.updatePremiere(request);

                // then -- 예상되는 변화 및 결과
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(premiere).isNotNull();
                    softly.assertThat(premiere.getTitle()).isEqualTo(request.title());
                    softly.assertThat(premiere.getEventImage()).isEqualTo(request.image());
                    softly.assertThat(premiere.isPaymentRequired()).isEqualTo(request.isPaymentRequired());
                    softly.assertThat(premiere.getStartAt()).isEqualTo(request.startAt());
                    softly.assertThat(premiere.getEndAt()).isEqualTo(request.endAt());
                });
            }
        }
    }
}