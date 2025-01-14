package net.pointofviews.review.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.exception.ReviewException;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.impl.ReviewAdminServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewAdminServiceTest {

    @InjectMocks
    private ReviewAdminServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    class BlindReview {

        @Nested
        class Success {

            @Test
            void 리뷰_숨김() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);
                Review review = mock(Review.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));
                given(reviewRepository.findById(any())).willReturn(Optional.of(review));

                // when -- 테스트하고자 하는 행동
                reviewService.blindReview(member, 1L, 1L);

                // then -- 예상되는 변화 및 결과
                verify(review).toggleDisabled();
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_관리자_MemberException_adminNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(member.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                    reviewService.blindReview(member, 1L, 1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage())
                        .isEqualTo(String.format("관리자(Id: %s)가 존재하지 않습니다.", memberId));
                });
            }

            @Test
            void 존재하지_않는_영화_MovieException_movieNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(movieRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MovieException exception = assertThrows(MovieException.class, () ->
                        reviewService.blindReview(member, -1L, 1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo("영화(Id: -1)는 존재하지 않습니다.");
                    verifyNoInteractions(reviewRepository);
                });
            }

            @Test
            void 존재하지_않는_리뷰_ReviewException_reviewNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Movie movie = mock(Movie.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(movieRepository.findById(any())).willReturn(Optional.of(movie));
                given(reviewRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                ReviewException exception = assertThrows(ReviewException.class, () ->
                        reviewService.blindReview(member, 1L, -1L)
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).contains("리뷰(Id: -1)는 존재하지 않습니다.");
                });
            }
        }
    }
}