package net.pointofviews.premiere.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.lock.DistributeLock;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Entry;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.dto.response.ReadEntryResponse;
import net.pointofviews.premiere.dto.response.ReadMyEntryListResponse;
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

    @Override
    @Transactional
    @DistributeLock(key = "#premiereId")
    public CreateEntryResponse saveEntry(Member loginMember, Long premiereId, CreateEntryRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        if (entryRepository.existsEntryByMemberIdAndPremiereId(member.getId(), premiereId)) {
            log.warn("[응모오류] 회원 ID: {}, 응모한 시사회 ID: {}", member.getId(), premiereId);
            throw duplicateEntry();
        }

        int requestTotalAmount = request.quantity() * request.amount();
        int premiereTotalAmount = request.quantity() * premiere.getAmount();

        if (requestTotalAmount != premiereTotalAmount) {
            log.warn("[응모오류] 요청한 수량의 총 금액: {}, 실제 총 금액: {}", requestTotalAmount, premiereTotalAmount);
            throw entryBadRequest();
        }

        Long currQuantity = entryRepository.countEntriesByPremiereId(premiereId);

        if (currQuantity + request.quantity() > premiere.getMaxQuantity()) {
            log.warn("[응모오류] 시사회 수량: {}, 초과된 시사회 수량: {}",
                    premiere.getMaxQuantity(),
                    currQuantity + request.quantity() - premiere.getMaxQuantity());

            throw quantityExceeded();
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
    public ReadMyEntryListResponse findMyEntryList(Member loginMember) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        List<ReadEntryResponse> entryList = entryRepository.findAllByMemberId(member.getId());

        return new ReadMyEntryListResponse(entryList);
    }
}
