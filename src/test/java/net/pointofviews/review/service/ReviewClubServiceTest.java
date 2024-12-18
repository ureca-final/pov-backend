package net.pointofviews.review.service;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.fixture.ReviewFixture;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.service.impl.ReviewClubServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewClubServiceTest {

    @InjectMocks
    private ReviewClubServiceImpl reviewClubService;

    @Mock
    private MemberClubRepository memberClubRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    class FindMyClubList {

        @Nested
        class Success {

            @Test
            void 가입한_클럽_목록_조회() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                given(memberRepository.findById(any())).willReturn(Optional.of(member));

                Club club = mock(Club.class);
                MemberClub memberClub1 = mock(MemberClub.class);
                MemberClub memberClub2 = mock(MemberClub.class);

                given(memberClub1.getClub()).willReturn(club);
                given(memberClub2.getClub()).willReturn(club);

                List<MemberClub> memberClubs = List.of(memberClub1, memberClub2);

                given(memberClubRepository.findClubsByMemberId(any())).willReturn(memberClubs);

                // when -- 테스트하고자 하는 행동
                ReadMyClubInfoListResponse result = reviewClubService.findMyClubList(member);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.clubs().size()).isEqualTo(2);
                });
            }

            @Test
            void 가입한_클럽이_없을_시_빈_목록_반환() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                List<MemberClub> memberClubs = List.of();

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(memberClubRepository.findClubsByMemberId(any())).willReturn(memberClubs);

                // when -- 테스트하고자 하는 행동
                ReadMyClubInfoListResponse result = reviewClubService.findMyClubList(member);

                // then -- 예상되는 변화 및 결과
                assertThat(result.clubs().size()).isEqualTo(0);
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_회원_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(member.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        reviewClubService.findMyClubList(member)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("회원(Id: %s)이 존재하지 않습니다.", memberId));
                });
            }
        }
    }

    @Nested
    class FindReviewByClub {

        @Nested
        class Success {

            @Test
            void 클럽별_리뷰_조회() {
                // given -- 테스트의 상태 설정
                Club club = mock(Club.class);
                UUID clubId = UUID.randomUUID();
                Member loginMember = mock(Member.class);


                given(clubRepository.findById(any())).willReturn(Optional.of(club));

                ReadReviewResponse review1 = ReviewFixture.readReviewResponse();
                ReadReviewResponse review2 = ReviewFixture.readReviewResponse();

                List<ReadReviewResponse> reviewList = List.of(review1, review2);
                Slice<ReadReviewResponse> reviews = new SliceImpl<>(reviewList);

                given(memberClubRepository.findReviewsWithLikesByClubId(any(), any(), any())).willReturn(reviews);

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                ReadMyClubReviewListResponse result = reviewClubService.findReviewByClub(clubId, loginMember.getId(), pageable);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.clubId()).isEqualTo(clubId);
                    softly.assertThat(result.reviews()).isEqualTo(reviews);
                    softly.assertThat(result.reviews().getSize()).isEqualTo(2);
                    softly.assertThat(result.reviews().getContent()).contains(review1, review2);
                });
            }

            @Test
            void 해당_클럽에_리뷰가_없을_시_빈_목록_반환() {
                // given -- 테스트의 상태 설정
                Club club = mock(Club.class);
                Member loginMember = mock(Member.class);
                UUID clubId = UUID.randomUUID();

                given(clubRepository.findById(any())).willReturn(Optional.of(club));

                Slice<ReadReviewResponse> reviews = new SliceImpl<>(List.of());

                given(memberClubRepository.findReviewsWithLikesByClubId(any(), any(), any())).willReturn(reviews);

                Pageable pageable = PageRequest.of(0, 10);

                // when -- 테스트하고자 하는 행동
                ReadMyClubReviewListResponse result = reviewClubService.findReviewByClub(clubId, loginMember.getId(), pageable);

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(result.clubId()).isEqualTo(clubId);
                    softly.assertThat(result.reviews()).isEmpty();
                    softly.assertThat(result.reviews().getSize()).isEqualTo(0);
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_클럽_ClubException_clubNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                UUID clubId = UUID.randomUUID();
                Member loginMember = mock(Member.class);

                given(clubRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                ClubException exception = assertThrows(ClubException.class, () ->
                        reviewClubService.findReviewByClub(clubId, loginMember.getId(), null)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("클럽(Id: %s)이 존재하지 않습니다.", clubId));
                    verify(memberClubRepository, times(0)).findReviewsWithLikesByClubId(any(), any(), any());
                });
            }
        }
    }
}