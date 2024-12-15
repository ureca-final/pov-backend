package net.pointofviews.premiere.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.dto.response.ReadMyEntryListResponse;

public interface EntryService {

    CreateEntryResponse saveEntry(Member loginMember, Long premiereId, CreateEntryRequest request);

    ReadMyEntryListResponse findMyEntryList(Member loginMember);
}
