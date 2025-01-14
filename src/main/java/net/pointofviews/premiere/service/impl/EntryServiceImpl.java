package net.pointofviews.premiere.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.lock.DistributeLock;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.payment.repository.TempPaymentRepository;
import net.pointofviews.premiere.domain.Entry;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.request.DeleteEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.dto.response.ReadEntryResponse;
import net.pointofviews.premiere.dto.response.ReadMyEntryListResponse;
import net.pointofviews.premiere.exception.EntryException;
import net.pointofviews.premiere.repository.EntryRepository;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.EntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static net.pointofviews.member.exception.MemberException.memberNotFound;
import static net.pointofviews.premiere.exception.EntryException.*;
import static net.pointofviews.premiere.exception.PremiereException.premiereNotFound;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final PremiereRepository premiereRepository;
    private final MemberRepository memberRepository;
    private final TempPaymentRepository tempPaymentRepository;

    @Override
    @DistributeLock(key = "#premiereId")
    public CreateEntryResponse saveEntry(Member loginMember, Long premiereId, CreateEntryRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        Long currQuantity = entryRepository.countEntriesByPremiereId(premiereId);
        if (currQuantity + request.quantity() > premiere.getMaxQuantity()) {
            log.warn("[응모오류] 수량 초과 - 시사회 수량: {}, 초과된 시사회 수량: {}",
                    premiere.getMaxQuantity(),
                    currQuantity + request.quantity() - premiere.getMaxQuantity());

            throw quantityExceeded();
        }

        if (entryRepository.existsEntryByMemberIdAndPremiereId(member.getId(), premiereId)) {
            log.warn("[응모오류] 응모 중복 - 회원 ID: {}, 응모한 시사회 ID: {}", member.getId(), premiereId);
            throw duplicateEntry();
        }

        int requestTotalAmount = request.quantity() * request.amount();
        int premiereTotalAmount = request.quantity() * premiere.getAmount();

        if (requestTotalAmount != premiereTotalAmount) {
            log.warn("[응모오류] 금액 불일치 - 요청한 수량의 총 금액: {}, 실제 총 금액: {}", requestTotalAmount, premiereTotalAmount);
            throw entryBadRequest();
        }

        String orderId = UUID.randomUUID().toString();

        Entry entry = Entry.builder()
                .member(member)
                .premiere(premiere)
                .orderId(orderId)
                .quantity(request.quantity())
                .amount(requestTotalAmount)
                .build();

        entryRepository.save(entry);

        return new CreateEntryResponse(entry.getOrderId());
    }

    @Override
    @Transactional
    public CreateEntryResponse saveEntry2(Member loginMember, Long premiereId, CreateEntryRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        Long currQuantity = entryRepository.countEntriesByPremiereId(premiereId);
        if (currQuantity + request.quantity() > premiere.getMaxQuantity()) {
            log.warn("[응모오류] 수량 초과 - 시사회 수량: {}, 초과된 시사회 수량: {}",
                    premiere.getMaxQuantity(),
                    currQuantity + request.quantity() - premiere.getMaxQuantity());

            throw quantityExceeded();
        }

        if (entryRepository.existsEntryByMemberIdAndPremiereId(member.getId(), premiereId)) {
            log.warn("[응모오류] 응모 중복 - 회원 ID: {}, 응모한 시사회 ID: {}", member.getId(), premiereId);
            throw duplicateEntry();
        }

        int requestTotalAmount = request.quantity() * request.amount();
        int premiereTotalAmount = request.quantity() * premiere.getAmount();

        if (requestTotalAmount != premiereTotalAmount) {
            log.warn("[응모오류] 금액 불일치 - 요청한 수량의 총 금액: {}, 실제 총 금액: {}", requestTotalAmount, premiereTotalAmount);
            throw entryBadRequest();
        }

        String orderId = UUID.randomUUID().toString();

        Entry entry = Entry.builder()
                .member(member)
                .premiere(premiere)
                .orderId(orderId)
                .quantity(request.quantity())
                .amount(requestTotalAmount)
                .build();

        entryRepository.save(entry);

        return new CreateEntryResponse(entry.getOrderId());
    }

    @Override
    @Transactional
    public void deleteEntry(Member loginMember, Long premiereId, DeleteEntryRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        if (premiereRepository.findById(premiereId).isEmpty()) {
            throw premiereNotFound(premiereId);
        }

        Entry entry = entryRepository.findEntryByOrderId(request.orderId())
                .orElseThrow(EntryException::entryNotFound);

        if (!entry.getMember().getId().equals(member.getId())) {
            log.warn("[응모오류] 응모자 불일치 - Entry 회원 ID: {}, 삭제 요청한 회원 ID: {}", entry.getMember().getId(), member.getId());
            throw unauthorizedEntry();
        }

        entryRepository.delete(entry);

        tempPaymentRepository.findByOrderId(request.orderId())
                .ifPresent(tempPaymentRepository::delete);

    }

    @Override
    public ReadMyEntryListResponse findMyEntryList(Member loginMember) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        List<ReadEntryResponse> entryList = entryRepository.findAllByMemberId(member.getId());

        return new ReadMyEntryListResponse(entryList);
    }
}
