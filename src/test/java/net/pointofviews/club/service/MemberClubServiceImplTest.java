package net.pointofviews.club.service;

import net.pointofviews.club.domain.MemberClub;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberClubServiceImplTest {

    @InjectMocks
    private MemberClubServiceImpl memberClubService;

    @Mock
    private MemberClubRepository memberClubRepository;

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
}