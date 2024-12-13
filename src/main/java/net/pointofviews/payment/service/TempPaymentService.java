package net.pointofviews.payment.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.payment.dto.TempPaymentDto;

public interface TempPaymentService {

    TempPaymentDto saveTempPayment(Member loginMember, TempPaymentDto request);
}
