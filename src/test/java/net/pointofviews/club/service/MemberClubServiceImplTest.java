package net.pointofviews.club.service;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.service.impl.ClubServiceImpl;
import net.pointofviews.member.domain.Member;
import net.pointofviews.club.dto.response.ReadClubMemberListResponse;
import net.pointofviews.club.dto.response.ReadClubMemberResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.club.service.impl.MemberClubServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberClubServiceImplTest {

    @InjectMocks
    private MemberClubServiceImpl memberClubService;

    @Mock
    private ClubServiceImpl clubService;

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

            when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
            when(memberClubRepository.findByClubIdAndMemberId(clubId, member.getId())).thenReturn(Optional.empty());

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

}