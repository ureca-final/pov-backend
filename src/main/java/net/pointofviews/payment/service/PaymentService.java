package net.pointofviews.payment.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.payment.dto.request.ConfirmPaymentRequest;

public interface PaymentService {

    void confirmPayment(Member loginMember, ConfirmPaymentRequest request);
}
