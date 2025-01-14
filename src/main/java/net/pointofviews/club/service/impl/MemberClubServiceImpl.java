package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.response.ClubMemberResponse;
import net.pointofviews.club.dto.response.ReadAllClubMembersResponse;
import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.club.service.MemberClubService;
import net.pointofviews.club.utils.InviteCodeGenerator;
import net.pointofviews.common.service.impl.StringRedisServiceImpl;
import net.pointofviews.common.utils.UuidUtils;
import net.pointofviews.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static net.pointofviews.club.exception.ClubException.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberClubServiceImpl implements MemberClubService {

    private final MemberClubRepository memberClubRepository;
    private final ClubRepository clubRepository;
    private final StringRedisServiceImpl redisService;
    private static final int INVITE_CODE_LENGTH = 8;
    private static final int DAY_IN_SECONDS = 60 * 60 * 24;
    private static final String CLUB_TO_INVITE_CODE_KEY_PREFIX = "club:invite:";
    private static final String INVITE_CODE_TO_CLUB_KEY_PREFIX = "invite:code:";

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
                .orElseThrow(() -> clubLeaderNotFound(clubId));
    }

    @Override
    @Transactional
    public void joinClub(UUID clubId, Member member) {
        // 클럽이 존재하는지 확인
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> clubNotFound(clubId));

        // 이미 가입된 사용자인지 확인
        boolean isAlreadyMember = memberClubRepository.findByClubIdAndMemberId(clubId, member.getId()).isPresent();
        if (isAlreadyMember) {
            throw memberAlreadyInClub();
        }

        if (memberClubRepository.countByClub(club) >= club.getMaxParticipants()) {
            throw ClubException.participantLimitReached();
        }

        // MemberClub 엔티티 생성 및 저장
        MemberClub memberClub = MemberClub.builder()
                .club(club)
                .member(member)
                .isLeader(false) // 기본 값은 일반 멤버
                .build();

        memberClubRepository.save(memberClub);
    }

    @Override
    public String generateInviteCode(UUID clubId, Member loginMember) {
        MemberClub memberClub = memberClubRepository.findByClubIdAndMemberId(clubId, loginMember.getId())
                .orElseThrow(() -> clubNotFound(clubId));

        if (!memberClub.isLeader()) {
            throw notClubLeader();
        }

        String clubKey = CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId;
        String baseUrl = "https://www.point-of-views.com/clubs";

        String existingInviteCode = redisService.getValue(clubKey);
        if (existingInviteCode != null) {
            return String.format("%s/code?value=%s", baseUrl, existingInviteCode);
        }

        String inviteCode;
        boolean isDuplicate;
        do {
            inviteCode = InviteCodeGenerator.generateInviteCode(INVITE_CODE_LENGTH);
            isDuplicate = !redisService.setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + inviteCode, clubId.toString(), Duration.ofSeconds(DAY_IN_SECONDS));
        } while (isDuplicate);

        redisService.setValue(clubKey, inviteCode, Duration.ofSeconds(DAY_IN_SECONDS));

        return String.format("%s/code?value=%s", baseUrl, inviteCode);
    }

    @Override
    public ReadAllClubMembersResponse readAllMembersByClubId(UUID clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw clubNotFound(clubId);
        }

        List<ClubMemberResponse> allMembersByClubId = memberClubRepository.findAllMembersByClubId(clubId);
        return new ReadAllClubMembersResponse(allMembersByClubId);
    }

    @Override
    @Transactional
    public String joinPrivateClub(Member loginMember, String value) {
        String inviteCode = INVITE_CODE_TO_CLUB_KEY_PREFIX + value;
        String stringClubId = redisService.getValue(inviteCode);

        if (stringClubId == null) {
            throw inviteCodeNotFound(value);
        }

        UUID clubId = UuidUtils.fromString(stringClubId);

        if (memberClubRepository.existsByClubIdAndMember(clubId, loginMember)) {
            throw memberAlreadyInClub();
        }

        Club proxyClub = Club.generateProxy(clubId);

        MemberClub newbie = MemberClub.builder()
                .club(proxyClub)
                .member(loginMember)
                .isLeader(false)
                .build();

        memberClubRepository.save(newbie);
        return stringClubId;
    }
}
