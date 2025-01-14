package net.pointofviews.club.service;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.club.service.impl.MemberClubServiceImpl;
import net.pointofviews.club.utils.InviteCodeGenerator;
import net.pointofviews.common.service.impl.StringRedisServiceImpl;
import net.pointofviews.member.domain.Member;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberClubServiceImplTest {

    @InjectMocks
    private MemberClubServiceImpl memberClubService;

    @Mock
    private StringRedisServiceImpl redisService;

    @Mock
    private MemberClubRepository memberClubRepository;

    @Mock
    private ClubRepository clubRepository;

    @Nested
    class ReadMembersByClubId {

        @Test
        void 클럽_멤버_조회_성공() {
            // given
            UUID clubId = UUID.randomUUID();
            List<ReadClubMemberResponse> members = List.of(
                    new ReadClubMemberResponse("nickname1", "https://example.com/image.jpg", false),
                    new ReadClubMemberResponse("nickname2", "https://example.com/image2.jpg", true)
            );

            given(memberClubRepository.findMembersByClubId(clubId)).willReturn(members);

            // when
            ReadClubMemberListResponse response = memberClubService.readMembersByClubId(clubId);

            // then
            assertThat(response.memberList()).hasSize(2);
            assertThat(response.memberList().get(0).nickname()).isEqualTo("nickname1");
            assertThat(response.memberList().get(1).isLeader()).isTrue();
            verify(memberClubRepository).findMembersByClubId(clubId);
        }
    }

    @Nested
    class IsMemberOfClub {

        @Test
        void 클럽_멤버_여부_확인_성공() {
            // given
            UUID clubId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            MemberClub memberClub = mock(MemberClub.class);
            given(memberClubRepository.findByClubIdAndMemberId(clubId, memberId)).willReturn(Optional.of(memberClub));

            // when
            boolean isMember = memberClubService.isMemberOfClub(clubId, memberId);

            // then
            assertThat(isMember).isTrue();
            verify(memberClubRepository).findByClubIdAndMemberId(clubId, memberId);
        }


        @Test
        void 클럽_멤버_여부_확인_실패() {
            // given
            UUID clubId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            given(memberClubRepository.findByClubIdAndMemberId(clubId, memberId)).willReturn(Optional.empty());

            // when
            boolean isMember = memberClubService.isMemberOfClub(clubId, memberId);

            // then
            assertThat(isMember).isFalse();
            verify(memberClubRepository).findByClubIdAndMemberId(clubId, memberId);
        }
    }

    @Nested
    class ReadClubLeaderByClubId {

        @Test
        void 클럽_리더_조회_성공() {
            // given
            UUID clubId = UUID.randomUUID();
            ReadClubMemberResponse leader = new ReadClubMemberResponse("leaderNickname", "https://example.com/image.jpg", true);

            given(memberClubRepository.findLeaderByClubId(clubId)).willReturn(Optional.of(leader));

            // when
            ReadClubMemberResponse response = memberClubService.readClubLeaderByClubId(clubId);

            // then
            assertThat(response.nickname()).isEqualTo("leaderNickname");
            assertThat(response.isLeader()).isTrue();
            verify(memberClubRepository).findLeaderByClubId(clubId);
        }

        @Test
        void 클럽_리더_조회_실패() {
            // given
            UUID clubId = UUID.randomUUID();

            given(memberClubRepository.findLeaderByClubId(clubId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberClubService.readClubLeaderByClubId(clubId))
                    .isInstanceOf(ClubException.class)
                    .hasMessageContaining(String.format("클럽(ID: %s)의 리더를 찾을 수 없습니다.", clubId));
        }
    }


    @Nested
    class JoinClub {

        @Test
        void 클럽_가입_성공() {
            // given
            Member member = mock(Member.class);
            Club club = mock(Club.class);
            UUID clubId = UUID.randomUUID();

            given(clubRepository.findById(clubId)).willReturn(Optional.of(club));
            given(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).willReturn(Optional.empty());
            given(memberClubRepository.countByClub(club)).willReturn(2L);
            given(club.getMaxParticipants()).willReturn(5);

            // when
            memberClubService.joinClub(clubId, member);

            // then
            verify(clubRepository).findById(clubId);
            verify(memberClubRepository).findByClubIdAndMemberId(clubId, member.getId());
            verify(memberClubRepository).save(any(MemberClub.class));
        }

        @Test
        void 클럽_가입_실패_클럽_존재하지_않음() {
            // given
            Member member = mock(Member.class);
            UUID clubId = UUID.randomUUID();

            when(clubRepository.findById(clubId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberClubService.joinClub(clubId, member))
                    .isInstanceOf(ClubException.class)
                    .hasMessage(String.format("클럽(Id: %s)이 존재하지 않습니다.", clubId));
            verify(clubRepository).findById(clubId);
            verify(memberClubRepository, never()).findByClubIdAndMemberId(clubId, member.getId());
            verify(memberClubRepository, never()).save(any(MemberClub.class));
        }

        @Test
        void 클럽_가입_실패_이미_가입된_사용자() {
            // given
            Member member = mock(Member.class);
            Club club = mock(Club.class);
            UUID clubId = UUID.randomUUID();

            when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
            when(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).thenReturn(Optional.of(MemberClub.builder().build()));

            // when & then
            assertThatThrownBy(() -> memberClubService.joinClub(clubId, member))
                    .isInstanceOf(ClubException.class)
                    .hasMessage("이미 클럽에 가입된 회원입니다.");
            verify(clubRepository).findById(clubId);
            verify(memberClubRepository).findByClubIdAndMemberId(clubId, member.getId());
            verify(memberClubRepository, never()).save(any(MemberClub.class));
        }
    }

    @Nested
    class GenerateInviteCode {
        private static final String CLUB_TO_INVITE_CODE_KEY_PREFIX = "club:invite:";
        private static final String INVITE_CODE_TO_CLUB_KEY_PREFIX = "invite:code:";

        @Nested
        class Success {

            @Test
            void 초대_코드_생성_성공_이미_존재하는_코드() {
                // given
                UUID clubId = UUID.randomUUID();
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                String existingInviteCode = "existingInviteCode";

                given(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).willReturn(Optional.of(memberClub));
                given(memberClub.isLeader()).willReturn(true);
                given(redisService.getValue(CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId)).willReturn(existingInviteCode);

                // when
                String result = memberClubService.generateInviteCode(clubId, member);

                // then
                assertThat(result).isEqualTo("https://www.point-of-views.com/clubs/code?value=" + existingInviteCode);
                then(memberClubRepository).should().findByClubIdAndMemberId(clubId, member.getId());
                then(redisService).should().getValue(CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId);
            }

            @Test
            void 초대_코드_생성_성공_새로운_코드_생성() {
                // given
                UUID clubId = UUID.randomUUID();
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                String newInviteCode = "newInviteCode";
                int dayInSeconds = 60 * 60 * 24;

                given(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).willReturn(Optional.of(memberClub));
                given(memberClub.isLeader()).willReturn(true);
                given(redisService.getValue(CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId)).willReturn(null);
                given(redisService.setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + newInviteCode, clubId.toString(), Duration.ofSeconds(dayInSeconds))).willReturn(true);

                try (MockedStatic<InviteCodeGenerator> inviteCodeGeneratorMock = mockStatic(InviteCodeGenerator.class)) {
                    inviteCodeGeneratorMock.when(() -> InviteCodeGenerator.generateInviteCode(8)).thenReturn(newInviteCode);

                    // when
                    String result = memberClubService.generateInviteCode(clubId, member);

                    // then
                    assertThat(result).isEqualTo("https://www.point-of-views.com/clubs/code?value=" + newInviteCode);
                    then(redisService).should().setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + newInviteCode, clubId.toString(), Duration.ofSeconds(dayInSeconds));
                    then(redisService).should().setValue(CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId, newInviteCode, Duration.ofSeconds(dayInSeconds));
                    then(memberClubRepository).should().findByClubIdAndMemberId(clubId, member.getId());
                }
            }

            @Test
            void 초대_코드_중복_발생_시_새로운_코드_생성() {
                // given
                UUID clubId = UUID.randomUUID();
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                String duplicateInviteCode = "duplicateCode";
                String newInviteCode = "uniqueCode";
                int dayInSeconds = 60 * 60 * 24;

                given(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).willReturn(Optional.of(memberClub));
                given(memberClub.isLeader()).willReturn(true);
                given(redisService.getValue(CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId)).willReturn(null);
                given(redisService.setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + duplicateInviteCode, clubId.toString(), Duration.ofSeconds(dayInSeconds))).willReturn(false);
                given(redisService.setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + newInviteCode, clubId.toString(), Duration.ofSeconds(dayInSeconds))).willReturn(true);

                try (MockedStatic<InviteCodeGenerator> inviteCodeGeneratorMock = mockStatic(InviteCodeGenerator.class)) {
                    inviteCodeGeneratorMock.when(() -> InviteCodeGenerator.generateInviteCode(8))
                            .thenReturn(duplicateInviteCode)
                            .thenReturn(newInviteCode);

                    // when
                    String result = memberClubService.generateInviteCode(clubId, member);

                    // then
                    assertThat(result).isEqualTo("https://www.point-of-views.com/clubs/code?value=" + newInviteCode);
                    then(redisService).should().setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + duplicateInviteCode, clubId.toString(), Duration.ofSeconds(dayInSeconds));
                    then(redisService).should().setIfAbsent(INVITE_CODE_TO_CLUB_KEY_PREFIX + newInviteCode, clubId.toString(), Duration.ofSeconds(dayInSeconds));
                    then(redisService).should().setValue(CLUB_TO_INVITE_CODE_KEY_PREFIX + clubId, newInviteCode, Duration.ofSeconds(dayInSeconds));
                }
            }
        }

        @Nested
        class Fail {

            @Test
            void 초대_코드_생성_실패_리더가_아님() {
                // given
                UUID clubId = UUID.randomUUID();
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                given(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).willReturn(Optional.of(memberClub));
                given(memberClub.isLeader()).willReturn(false);

                // when & then
                assertThatThrownBy(() -> memberClubService.generateInviteCode(clubId, member))
                        .isInstanceOf(ClubException.class)
                        .hasMessage(ClubException.notClubLeader().getMessage());
                then(memberClubRepository).should().findByClubIdAndMemberId(clubId, member.getId());
                then(redisService).shouldHaveNoInteractions();
            }

            @Test
            void 초대_코드_생성_실패_클럽_존재하지_않음() {
                // given
                UUID clubId = UUID.randomUUID();
                Member member = mock(Member.class);

                given(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> memberClubService.generateInviteCode(clubId, member))
                        .isInstanceOf(ClubException.class)
                        .hasMessage(ClubException.clubNotFound(clubId).getMessage());
                then(memberClubRepository).should().findByClubIdAndMemberId(clubId, member.getId());
                then(redisService).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    class JoinPrivateClub {
        @Nested
        class Success {

            @Test
            void 초대_코드로_클럽_가입_성공() {
                // given
                Member member = mock(Member.class);
                UUID clubId = UUID.randomUUID();
                String inviteCode = "validInviteCode";
                String redisKey = "invite:code:" + inviteCode;

                given(redisService.getValue(redisKey)).willReturn(clubId.toString());
                given(memberClubRepository.existsByClubIdAndMember(clubId, member)).willReturn(false);

                // when
                memberClubService.joinPrivateClub(member, inviteCode);

                // then
                verify(redisService).getValue(redisKey);
                verify(memberClubRepository).existsByClubIdAndMember(clubId, member);
                verify(memberClubRepository).save(any(MemberClub.class));
            }
        }

        @Nested
        class Failure {

            @Test
            void 초대_코드가_유효하지_않음() {
                // given
                Member member = mock(Member.class);
                String inviteCode = "invalidInviteCode";
                String redisKey = "invite:code:" + inviteCode;

                given(redisService.getValue(redisKey)).willReturn(null);

                // when & then
                assertThatThrownBy(() -> memberClubService.joinPrivateClub(member, inviteCode))
                        .isInstanceOf(ClubException.class)
                        .hasMessageContaining(ClubException.inviteCodeNotFound(inviteCode).getMessage());
                verify(redisService).getValue(redisKey);
                verify(memberClubRepository, never()).existsByClubIdAndMember(any(), any());
                verify(memberClubRepository, never()).save(any());
            }

            @Test
            void 이미_클럽에_가입된_사용자() {
                // given
                Member member = mock(Member.class);
                UUID clubId = UUID.randomUUID();
                String inviteCode = "validInviteCode";
                String redisKey = "invite:code:" + inviteCode;

                given(redisService.getValue(redisKey)).willReturn(clubId.toString());
                given(memberClubRepository.existsByClubIdAndMember(clubId, member)).willReturn(true);

                // when & then
                assertThatThrownBy(() -> memberClubService.joinPrivateClub(member, inviteCode))
                        .isInstanceOf(ClubException.class)
                        .hasMessage("이미 클럽에 가입된 회원입니다.");
                verify(redisService).getValue(redisKey);
                verify(memberClubRepository).existsByClubIdAndMember(clubId, member);
                verify(memberClubRepository, never()).save(any());
            }
        }
    }
}