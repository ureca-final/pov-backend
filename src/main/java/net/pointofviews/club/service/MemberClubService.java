package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.member.domain.Member;

import java.util.UUID;

public interface MemberClubService {
    ReadClubMemberListResponse readMembersByClubId(UUID clubId);

    boolean isMemberOfClub(UUID clubId, UUID memberId);

    ReadClubMemberResponse readClubLeaderByClubId(UUID clubId);

    void joinClub(UUID clubId, Member member);

    String generateInviteCode(UUID clubId, Member loginMember);
}
