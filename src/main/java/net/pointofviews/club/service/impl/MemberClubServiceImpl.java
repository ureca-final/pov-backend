package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.club.service.MemberClubService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberClubServiceImpl implements MemberClubService {

    private final MemberClubRepository memberClubRepository;

    @Override
    public ReadClubMemberListResponse readMembersByClubId(UUID clubId) {
        List<ReadClubMemberResponse> members = memberClubRepository.findMembersByClubId(clubId);
        return new ReadClubMemberListResponse(members);
    }

    @Override
    public boolean isMemberOfClub(UUID clubId, UUID memberId) {
        return memberClubRepository.findByClubIdAndMemberId(clubId, memberId).isPresent();
    }

    @Override
    public ReadClubMemberResponse readClubLeaderByClubId(UUID clubId) {
        return memberClubRepository.findLeaderByClubId(clubId)
                .orElseThrow(() -> ClubException.clubLeaderNotFound(clubId));
    }
}
