package net.pointofviews.premiere.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.request.DeleteEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.dto.response.ReadMyEntryListResponse;

public interface EntryService {

    CreateEntryResponse saveEntry(Member loginMember, Long premiereId, CreateEntryRequest request);

    CreateEntryResponse saveEntry2(Member loginMember, Long premiereId, CreateEntryRequest request);

    void deleteEntry(Member loginMember, Long premiereId, DeleteEntryRequest request);

    ReadMyEntryListResponse findMyEntryList(Member loginMember);
}
