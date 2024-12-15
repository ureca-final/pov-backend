package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.club.service.MemberClubService;
import net.pointofviews.club.utils.InviteCodeGenerator;
import net.pointofviews.common.exception.RedisException;
import net.pointofviews.member.domain.Member;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;
    private static final int INVITE_CODE_LENGTH = 8;
    private static final int DAY_IN_SECONDS = 60 * 60 * 24;
    private static final String INVITE_CODE_PREFIX = "invitecode:";

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

        String key = INVITE_CODE_PREFIX + clubId;
        String baseUrl = "https://point-of-views.com/api/clubs";

        try {
            String existingInviteCode = redisTemplate.opsForValue().get(key);
            if (existingInviteCode != null) {
                return String.format("%s/%s/join?code=%s", baseUrl, clubId, existingInviteCode);
            }

            String inviteCode = InviteCodeGenerator.generateInviteCode(INVITE_CODE_LENGTH);

            redisTemplate.opsForValue().set(key, inviteCode, Duration.ofSeconds(DAY_IN_SECONDS));

            return String.format("%s/%s/join?code=%s", baseUrl, clubId, inviteCode);
        } catch (Exception e) {
            log.error("초대 코드 생성에 실패했습니다.", e);
            throw RedisException.redisServerError();
        }
    }
}
