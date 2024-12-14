package net.pointofviews.payment.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.payment.domain.TempPayment;
import net.pointofviews.payment.dto.TempPaymentDto;
import net.pointofviews.payment.repository.TempPaymentRepository;
import net.pointofviews.payment.service.TempPaymentService;
import net.pointofviews.premiere.domain.Entry;
import net.pointofviews.premiere.exception.EntryException;
import net.pointofviews.premiere.repository.EntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.pointofviews.member.exception.MemberException.memberNotFound;
import static net.pointofviews.payment.domain.OrderType.NORMAL;
import static net.pointofviews.payment.exception.PaymentException.paymentMismatch;

@Service
@RequiredArgsConstructor
public class TempPaymentServiceImpl implements TempPaymentService {

    private final TempPaymentRepository tempPaymentRepository;
    private final EntryRepository entryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public TempPaymentDto saveTempPayment(Member loginMember, TempPaymentDto request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        Entry entry = entryRepository.findEntryByOrderId(request.orderId())
                .orElseThrow(EntryException::entryNotFound);

        if (entry.getMember().getId().equals(member.getId())) {
            throw paymentMismatch();
        }

        TempPayment tempPayment = TempPayment.builder()
                .member(member)
                .type(NORMAL)
                .orderId(request.orderId())
                .amount(request.amount())
                .build();

        tempPaymentRepository.save(tempPayment);

        return request;
    }

}