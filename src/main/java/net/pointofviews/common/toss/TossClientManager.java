package net.pointofviews.common.toss;

import lombok.RequiredArgsConstructor;
import net.pointofviews.payment.dto.request.ConfirmPaymentRequest;
import net.pointofviews.payment.dto.response.ConfirmPaymentResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TossClientManager {

    private final TossClient tossClient;
    private final TossProperty tossProperty;

    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest request) {
        String authorization = tossProperty.base64SecretKey();

        return tossClient.confirm(authorization, request);
    }

    public void cancelPayment(String paymentKey, String cancelReason) {
        String authorization = tossProperty.base64SecretKey();

        tossClient.cancel(authorization, paymentKey, cancelReason);
    }
}
