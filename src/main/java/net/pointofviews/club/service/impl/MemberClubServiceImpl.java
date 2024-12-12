package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.club.service.MemberClubService;
import net.pointofviews.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberClubServiceImpl implements MemberClubService {

    private final MemberClubRepository memberClubRepository;
    private final ClubRepository clubRepository;

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

    @Override
    @Transactional
    public void joinClub(UUID clubId, Member member) {
        // 클럽이 존재하는지 확인
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> ClubException.clubNotFound(clubId));

        // 이미 가입된 사용자인지 확인
        boolean isAlreadyMember = memberClubRepository.findByClubIdAndMemberId(clubId, member.getId()).isPresent();
        if (isAlreadyMember) {
            throw ClubException.memberAlreadyInClub();
        }

        // MemberClub 엔티티 생성 및 저장
        MemberClub memberClub = MemberClub.builder()
                .club(club)
                .member(member)
                .isLeader(false) // 기본 값은 일반 멤버
                .build();

        memberClubRepository.save(memberClub);
    }
}
