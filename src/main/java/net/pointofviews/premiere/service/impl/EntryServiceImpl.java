package net.pointofviews.premiere.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.lock.DistributeLock;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Entry;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.repository.EntryRepository;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.EntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static net.pointofviews.member.exception.MemberException.memberNotFound;
import static net.pointofviews.premiere.exception.EntryException.*;
import static net.pointofviews.premiere.exception.PremiereException.premiereNotFound;

@Service
@RequiredArgsConstructor
@Transactional
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
            throw duplicateEntry();
        }

        if (request.amount() != premiere.getAmount()) {
            throw entryBadRequest();
        }

        Long currQuantity = entryRepository.countEntriesByPremiereId(premiereId);

        if (currQuantity + request.quantity() > premiere.getMaxQuantity()) {
            throw quantityExceeded();
        }

        String orderId = UUID.randomUUID().toString();

        Entry entry = Entry.builder()
                .member(member)
                .premiere(premiere)
                .orderId(orderId)
                .quantity(request.quantity())
                .amount(request.amount())
                .build();

        entryRepository.save(entry);

        return new CreateEntryResponse(entry.getOrderId());
    }

}
