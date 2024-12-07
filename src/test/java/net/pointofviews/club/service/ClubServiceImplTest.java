package net.pointofviews.club.service;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.ClubFavorGenre;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.request.PutClubLeaderRequest;
import net.pointofviews.club.dto.request.PutClubRequest;
import net.pointofviews.club.dto.response.CreateClubResponse;
import net.pointofviews.club.dto.response.PutClubLeaderResponse;
import net.pointofviews.club.dto.response.PutClubResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubFavorGenreRepository;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.common.exception.CommonCodeException;
import net.pointofviews.common.exception.S3Exception;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceImplTest {
    @InjectMocks
    private ClubServiceImpl clubService;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberClubRepository memberClubRepository;

    @Mock
    private ClubFavorGenreRepository clubFavorGenreRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommonCodeService commonCodeService;

    @Mock
    private S3Service s3Service;

    @Nested
    class saveClub {
        @Nested
        class Success {
            @Test
            void 클럽_생성_성공() {
                // given
                Member member = mock(Member.class);
                Club club = mock(Club.class);
                UUID clubId = UUID.randomUUID();

                CreateClubRequest request = new CreateClubRequest(
                        "테스트 클럽",
                        "설명",
                        10,
                        true,
                        List.of("액션", "로맨스"),
                        "club-image-url"
                );

                given(clubRepository.save(any())).willReturn(club);
                given(club.getId()).willReturn(clubId);
                given(club.getName()).willReturn(request.name());
                given(club.getDescription()).willReturn(request.description());
                given(club.getMaxParticipants()).willReturn(request.maxParticipants());
                given(club.isPublic()).willReturn(request.isPublic());
                given(commonCodeService.convertNameToCommonCode(anyString(), any())).willReturn("01");

                // when & then
                assertSoftly(softly -> {
                    CreateClubResponse response = clubService.saveClub(request, member);

                    softly.assertThat(response.clubId()).isEqualTo(clubId);
                    softly.assertThat(response.name()).isEqualTo(request.name());
                    softly.assertThat(response.maxParticipants()).isEqualTo(request.maxParticipants());
                    softly.assertThat(response.isPublic()).isEqualTo(request.isPublic());

                    verify(clubRepository).save(any(Club.class));
                    verify(memberClubRepository).save(any(MemberClub.class));
                    verify(clubFavorGenreRepository, times(2)).save(any(ClubFavorGenre.class));
                });
            }

            @Test
            void 이미지없이_클럽_생성_성공() {
                // given
                CreateClubRequest request = new CreateClubRequest(
                        "테스트 클럽",
                        "설명",
                        10,
                        true,
                        List.of("액션"),
                        ""
                );

                given(clubRepository.save(any())).willReturn(mock(Club.class));
                given(commonCodeService.convertNameToCommonCode(anyString(), any())).willReturn("01");

                // when
                CreateClubResponse response = clubService.saveClub(request, mock(Member.class));

                // then
                verify(clubRepository).save(any(Club.class));
                verify(s3Service, never()).moveImage(anyString(), anyString());
            }
        }

        @Nested
        class Failure {
            @Test
            void 잘못된_이미지_경로로_클럽생성시_실패() {
                // given
                CreateClubRequest request = new CreateClubRequest(
                        "테스트 클럽",
                        "설명",
                        10,
                        true,
                        List.of("액션"),
                        "invalid-image-url"
                );

                given(clubRepository.save(any())).willReturn(mock(Club.class));
                doThrow(S3Exception.failedToMove("이미지 이동 실패"))
                        .when(s3Service).moveImage(any(), any());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.saveClub(request, mock(Member.class)))
                            .isInstanceOf(S3Exception.class);
                });
            }

            @Test
            void 잘못된_장르코드_변환시_실패() {
                // given
                CreateClubRequest request = new CreateClubRequest(
                        "테스트 클럽",
                        "설명",
                        10,
                        true,
                        List.of("잘못된장르"),
                        ""
                );

                given(commonCodeService.convertNameToCommonCode(anyString(), any()))
                        .willThrow(CommonCodeException.class);

                // when & then
                assertThrows(CommonCodeException.class,
                        () -> clubService.saveClub(request, mock(Member.class)));
            }
        }
    }

    @Nested
    class UpdateClub {
        @Nested
        class Success {
            @Test
            void 클럽_정보_수정_성공() {
                // given
                UUID clubId = UUID.randomUUID();
                UUID memberId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                PutClubRequest request = new PutClubRequest(
                        "수정된 클럽명",
                        "수정된 설명",
                        20,
                        false,
                        List.of("액션", "로맨스"),
                        "new-image-url"
                );

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(memberClub.getMember()).willReturn(member);
                given(member.getId()).willReturn(memberId);
                given(memberClub.isLeader()).willReturn(true);
                given(club.getId()).willReturn(clubId);
                given(commonCodeService.convertNameToCommonCode(anyString(), any())).willReturn("01");

                // when
                PutClubResponse response = clubService.updateClub(clubId, request, member);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.id()).isEqualTo(clubId);

                    verify(club).updateClub(
                            request.name(),
                            request.description(),
                            request.maxParticipants(),
                            request.isPublic()
                    );
                    verify(club).updateClubImage(request.clubImage());
                    verify(clubFavorGenreRepository, times(1)).save(any(ClubFavorGenre.class));
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_클럽() {
                // given
                UUID clubId = UUID.randomUUID();
                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(
                                    () -> clubService.updateClubLeader(clubId, new PutClubLeaderRequest("email"), mock(Member.class)))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 클럽장이_아닌_경우() {
                // given
                UUID clubId = UUID.randomUUID();
                UUID memberId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member currentLeader = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(currentLeader.getId()).willReturn(memberId);
                given(memberClub.getMember()).willReturn(currentLeader);
                given(memberClub.isLeader()).willReturn(false);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(
                                    () -> clubService.updateClubLeader(clubId, new PutClubLeaderRequest("email"), currentLeader))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 잘못된_장르코드_변환시_실패() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                List<MemberClub> memberClubs = List.of(memberClub);

                PutClubRequest request = new PutClubRequest(
                        "수정된 클럽명",
                        "수정된 설명",
                        20,
                        false,
                        List.of("시사교양"),
                        "image-url"
                );

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(memberClubs);
                given(memberClub.getMember()).willReturn(member);
                given(member.getId()).willReturn(UUID.randomUUID());
                given(memberClub.isLeader()).willReturn(true);
                given(commonCodeService.convertNameToCommonCode(anyString(), any()))
                        .willThrow(CommonCodeException.class);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.updateClub(clubId, request, member))
                            .isInstanceOf(CommonCodeException.class);
                });
            }
        }
    }

    @Nested
    class updateClubLeader {
        @Nested
        class Success {
            @Test
            void 클럽장_변경_성공() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member currentLeader = mock(Member.class);
                Member newLeader = mock(Member.class);
                MemberClub currentLeaderClub = mock(MemberClub.class);
                MemberClub newLeaderClub = mock(MemberClub.class);

                String newLeaderEmail = "newleader@test.com";
                String newLeaderNickname = "새클럽장";
                PutClubLeaderRequest request = new PutClubLeaderRequest(newLeaderEmail);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getId()).willReturn(clubId);
                given(club.getMemberClubs()).willReturn(List.of(currentLeaderClub, newLeaderClub));

                given(memberRepository.findByEmail(newLeaderEmail)).willReturn(Optional.of(newLeader));
                given(newLeader.getEmail()).willReturn(newLeaderEmail);
                given(newLeader.getNickname()).willReturn(newLeaderNickname);
                given(newLeader.getId()).willReturn(UUID.randomUUID());

                given(currentLeaderClub.getMember()).willReturn(currentLeader);
                given(currentLeader.getId()).willReturn(UUID.randomUUID());
                given(currentLeaderClub.isLeader()).willReturn(true);

                given(newLeaderClub.getMember()).willReturn(newLeader);

                // when
                PutClubLeaderResponse response = clubService.updateClubLeader(clubId, request, currentLeader);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.clubId()).isEqualTo(clubId);
                    softly.assertThat(response.newLeaderEmail()).isEqualTo(newLeaderEmail);
                    softly.assertThat(response.newLeaderNickname()).isEqualTo(newLeaderNickname);

                    verify(currentLeaderClub).updateLeaderStatus(false);
                    verify(newLeaderClub).updateLeaderStatus(true);
                    verify(memberRepository).findByEmail(newLeaderEmail);
                });
            }

        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_클럽() {
                // given
                UUID clubId = UUID.randomUUID();
                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(
                                    () -> clubService.updateClubLeader(clubId, new PutClubLeaderRequest("email"), mock(Member.class)))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 클럽장이_아닌_경우() {
                // given
                UUID clubId = UUID.randomUUID();
                UUID memberId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member currentLeader = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(currentLeader.getId()).willReturn(memberId);
                given(memberClub.getMember()).willReturn(currentLeader);
                given(memberClub.isLeader()).willReturn(false);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(
                                    () -> clubService.updateClubLeader(clubId, new PutClubLeaderRequest("email"), currentLeader))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 존재하지_않는_새_클럽장() {
                // given
                UUID clubId = UUID.randomUUID();
                UUID memberId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member currentLeader = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(currentLeader.getId()).willReturn(memberId);
                given(memberClub.getMember()).willReturn(currentLeader);
                given(memberClub.isLeader()).willReturn(true);
                given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(
                                    () -> clubService.updateClubLeader(clubId, new PutClubLeaderRequest("email"), currentLeader))
                            .isInstanceOf(MemberException.class);
                });
            }
        }
    }

    @Nested
    class deleteClub {
        @Nested
        class Success {
            @Test
            void 클럽_삭제_성공() {
                // given
                UUID clubId = UUID.randomUUID();
                UUID memberId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                String clubImage = "club-image-url";

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(member.getId()).willReturn(memberId);
                given(memberClub.getMember()).willReturn(member);
                given(memberClub.isLeader()).willReturn(true);
                given(club.getClubImage()).willReturn(clubImage);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> clubService.deleteClub(clubId, member))
                            .doesNotThrowAnyException();

                    verify(s3Service).deleteImage(anyString());
                    verify(clubFavorGenreRepository).deleteAllByClub(club);
                    verify(memberClubRepository).deleteAllByClub(club);
                    verify(clubRepository).delete(club);
                });
            }

            @Test
            void 이미지가_없는_클럽_삭제_성공() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                UUID memberId = UUID.randomUUID();

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(member.getId()).willReturn(memberId);
                given(memberClub.getMember()).willReturn(member);
                given(memberClub.isLeader()).willReturn(true);
                given(club.getClubImage()).willReturn(null);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() -> clubService.deleteClub(clubId, member))
                            .doesNotThrowAnyException();

                    verify(s3Service, never()).deleteImage(anyString());
                    verify(clubFavorGenreRepository).deleteAllByClub(club);
                    verify(memberClubRepository).deleteAllByClub(club);
                    verify(clubRepository).delete(club);
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_클럽() {
                // given
                UUID clubId = UUID.randomUUID();
                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.deleteClub(clubId, mock(Member.class)))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 클럽장이_아닌_경우() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);
                UUID memberId = UUID.randomUUID();

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(club.getMemberClubs()).willReturn(List.of(memberClub));
                given(member.getId()).willReturn(memberId);
                given(memberClub.getMember()).willReturn(member);
                given(memberClub.isLeader()).willReturn(false);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.deleteClub(clubId, member))
                            .isInstanceOf(ClubException.class);
                });
            }
        }
    }

    @Nested
    class leaveClub {
        @Nested
        class Success {
            @Test
            void 일반_회원이_클럽_탈퇴_성공() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(memberClubRepository.findByClubAndMember(club, member)).willReturn(Optional.of(memberClub));
                given(memberClub.isLeader()).willReturn(false);

                // when
                clubService.leaveClub(clubId, member);

                // then
                verify(memberClubRepository).delete(memberClub);
            }

            @Test
            void 클럽장이_마지막_회원일때_클럽_탈퇴_성공() {
                // given
                UUID clubId = UUID.randomUUID();
                Member member = mock(Member.class);
                ClubService clubServiceMock = mock(ClubService.class);

                // when
                clubServiceMock.leaveClub(clubId, member);

                // then
                verify(clubServiceMock).leaveClub(clubId, member);
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_클럽() {
                // given
                UUID clubId = UUID.randomUUID();
                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.leaveClub(clubId, mock(Member.class)))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 클럽_멤버가_아닌_경우() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(memberClubRepository.findByClubAndMember(club, member)).willReturn(Optional.empty());

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.leaveClub(clubId, member))
                            .isInstanceOf(ClubException.class);
                });
            }

            @Test
            void 클럽장이_마지막_회원이_아닌데_탈퇴하려는_경우() {
                // given
                UUID clubId = UUID.randomUUID();
                Club club = mock(Club.class);
                Member member = mock(Member.class);
                MemberClub memberClub = mock(MemberClub.class);

                given(clubRepository.findByIdWithMemberClubs(clubId)).willReturn(Optional.of(club));
                given(memberClubRepository.findByClubAndMember(club, member)).willReturn(Optional.of(memberClub));
                given(memberClub.isLeader()).willReturn(true);
                given(memberClubRepository.countByClub(club)).willReturn(2L);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> clubService.leaveClub(clubId, member))
                            .isInstanceOf(ClubException.class);
                });
            }
        }
    }
}
