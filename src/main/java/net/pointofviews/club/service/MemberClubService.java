package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;

import java.util.UUID;

public interface MemberClubService {
    ReadClubMemberListResponse readMembersByClubId(UUID clubId);

    boolean isMemberOfClub(UUID clubId, UUID memberId);
    ReadClubMemberResponse readClubLeaderByClubId(UUID clubId);
}
