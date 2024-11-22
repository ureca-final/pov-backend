package net.pointofviews.payment.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void Payment_객체_생성() {
                // given
                Member member = Mockito.mock(Member.class);
                String paymentKey = "test_payment_key_123";
                String vendor = "TOSS";
                Integer price = 10000;

                // when
                Payment payment = Payment.builder()
                        .member(member)
                        .paymentKey(paymentKey)
                        .vendor(vendor)
                        .price(price)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(payment).isNotNull();
                    softly.assertThat(payment.getMember()).isEqualTo(member);
                    softly.assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
                    softly.assertThat(payment.getVendor()).isEqualTo(vendor);
                    softly.assertThat(payment.getPrice()).isEqualTo(price);
                });
            }
        }

        @Nested
        class failure {
            @Test
            void 결제키_없음_IllegalArgumentException_예외발생() {
                // given
                Member member = Mockito.mock(Member.class);
                String vendor = "TOSS";
                Integer price = 10000;

                // when & then
                assertThatThrownBy(() -> Payment.builder()
                        .member(member)
                        .vendor(vendor)
                        .price(price)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Payment key must not be null");
            }
        }
    }
}