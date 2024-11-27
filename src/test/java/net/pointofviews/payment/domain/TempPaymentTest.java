package net.pointofviews.payment.domain;

import net.pointofviews.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TempPaymentTest {
    @Nested
    class Constructor {

        @Nested
        class success {

            @Test
            void TempPayment_객체_생성() {
                // given
                Member member = Mockito.mock(Member.class);
                String orderId = "order_123";
                OrderType type = OrderType.NORMAL;
                Integer amount = 10000;

                // when
                TempPayment tempPayment = TempPayment.builder()
                        .member(member)
                        .orderId(orderId)
                        .type(type)
                        .amount(amount)
                        .build();
                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(tempPayment).isNotNull();
                    softly.assertThat(tempPayment.getMember()).isEqualTo(member);
                    softly.assertThat(tempPayment.getOrderId()).isEqualTo(orderId);
                    softly.assertThat(tempPayment.getType()).isEqualTo(type);
                    softly.assertThat(tempPayment.getAmount()).isEqualTo(amount);
                });
            }
        }

        @Nested
        class failure {
            @Test
            void 주문ID_없음_IllegalArgumentException_예외발생() {
                // given
                Member member = Mockito.mock(Member.class);
                OrderType type = OrderType.NORMAL;
                Integer amount = 10000;

                // when & then
                assertThatThrownBy(() -> TempPayment.builder()
                        .member(member)
                        .type(type)
                        .amount(amount)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Order ID must not be null");
            }
        }
    }
}