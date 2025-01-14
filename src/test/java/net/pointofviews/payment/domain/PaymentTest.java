package net.pointofviews.payment.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void Payment_객체_생성() {
                // given
                Member member = Mockito.mock(Member.class);
                String paymentKey = "test_payment_key_123";
                String vendor = "TOSS";
                Integer amount = 10000;

                // when
                Payment payment = Payment.builder()
                        .paymentKey(paymentKey)
                        .vendor(vendor)
                        .amount(amount)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(payment).isNotNull();
                    softly.assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
                    softly.assertThat(payment.getVendor()).isEqualTo(vendor);
                    softly.assertThat(payment.getAmount()).isEqualTo(amount);
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 결제키_없음_IllegalArgumentException_예외발생() {
                // given
                String vendor = "TOSS";
                Integer amount = 10000;

                // when & then
                assertThatThrownBy(() -> Payment.builder()
                        .vendor(vendor)
                        .amount(amount)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Payment key must not be null");
            }
        }
    }
}