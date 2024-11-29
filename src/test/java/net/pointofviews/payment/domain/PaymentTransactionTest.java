package net.pointofviews.payment.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionTest {

    @Nested
    class Constructor {

        @Nested
        class Success {

            @Test
            void PaymentTransaction_객체_생성() {
                // given
                Payment payment = Mockito.mock(Payment.class);
                String transactionKey = "transaction_key_123";
                PaymentType type = PaymentType.PAY;
                PaymentStatus status = PaymentStatus.SUCCESS;
                Integer amount = 10000;

                // when
                PaymentTransaction transaction = PaymentTransaction.builder()
                        .payment(payment)
                        .transactionKey(transactionKey)
                        .type(type)
                        .status(status)
                        .amount(amount)
                        .build();

                // then
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(transaction).isNotNull();
                    softly.assertThat(transaction.getPayment()).isEqualTo(payment);
                    softly.assertThat(transaction.getTransactionKey()).isEqualTo(transactionKey);
                    softly.assertThat(transaction.getType()).isEqualTo(type);
                    softly.assertThat(transaction.getStatus()).isEqualTo(status);
                    softly.assertThat(transaction.getAmount()).isEqualTo(amount);
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 거래키_없음_IllegalArgumentException_예외발생() {
                // given
                Payment payment = Mockito.mock(Payment.class);
                PaymentType type = PaymentType.PAY;
                PaymentStatus status = PaymentStatus.SUCCESS;
                Integer amount = 10000;

                // when & then
                assertThatThrownBy(() -> PaymentTransaction.builder()
                        .payment(payment)
                        .type(type)
                        .status(status)
                        .amount(amount)
                        .build())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Transaction key must not be null");
            }
        }
    }
}