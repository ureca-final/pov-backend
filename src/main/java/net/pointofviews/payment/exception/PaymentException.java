package net.pointofviews.payment.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PaymentException extends BusinessException {

    public PaymentException(HttpStatus status, String message) {
        super(status, message);
    }

    public static PaymentException paymentMismatch() {
        return new PaymentException(HttpStatus.FORBIDDEN, "결제자와 응모자가 동일하지 않습니다.");
    }

    public static PaymentException tempPaymentNotFound() {
        return new PaymentException(HttpStatus.NOT_FOUND, "임시 저장된 결제 내역이 존재하지 않습니다.");
    }

    public static PaymentException amountMismatch() {
        return new PaymentException(HttpStatus.BAD_REQUEST, "실제 결제 금액과 임시 결제 금액이 일치하지 않습니다.");
    }

}
