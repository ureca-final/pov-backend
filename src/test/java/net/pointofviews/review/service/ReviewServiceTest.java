package net.pointofviews.review.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.exception.ReviewNotFoundException;
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

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.exception.MovieNotFoundException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ReviewLikeRepository reviewLikeRepository;

	@Mock
	private ReviewLikeCountRepository reviewLikeCountRepository;

	@Mock
	private ReviewKeywordLinkRepository reviewKeywordLinkRepository;

	@Nested
	class SaveReview {
		@Nested
		class Success {
			@Test
			void 리뷰_등록_성공() {
				// given
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));

				CreateReviewRequest request = new CreateReviewRequest(
						"제목",
						"내용",
						"긍정적",
						List.of("01", "02"),
						false
				);

				// when & then
				assertSoftly(softly -> {
					softly.assertThatCode(() -> reviewService.saveReview(1L, request))
							.doesNotThrowAnyException();
				});
			}

			@Test
			void 키워드없이_리뷰_등록_성공() {
				// given
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));

				// null 케이스
				CreateReviewRequest nullRequest = new CreateReviewRequest(
						"제목",
						"내용",
						"긍정적",
						null,
						false
				);

				// 빈 리스트 케이스
				CreateReviewRequest emptyRequest = new CreateReviewRequest(
						"제목",
						"내용",
						"긍정적",
						List.of(),
						false
				);

				// when & then
				assertSoftly(softly -> {
					softly.assertThatCode(() -> reviewService.saveReview(1L, nullRequest))
							.doesNotThrowAnyException();
					softly.assertThatCode(() -> reviewService.saveReview(1L, emptyRequest))
							.doesNotThrowAnyException();
				});
			}
		}

		@Nested
		class Failure {
			@Test
			void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
				// given
				given(movieRepository.findById(any())).willReturn(Optional.empty());

				CreateReviewRequest request = new CreateReviewRequest(
						"제목",
						"내용",
						"긍정적",
						List.of("01"),
						false
				);

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.saveReview(1L, request))
							.isInstanceOf(MovieNotFoundException.class);
				});
			}
		}
	}

	@Nested
	class UpdateReview {
		@Nested
		class Success {
			@Test
			void 리뷰_수정_성공() {
				// given
				Movie movie = mock(Movie.class);
				Review review = mock(Review.class);

				given(movieRepository.findById(any())).willReturn(Optional.of(movie));
				given(reviewRepository.findById(any())).willReturn(Optional.of(review));

				PutReviewRequest request = new PutReviewRequest(
						"수정된 제목",
						"수정된 내용"
				);

				// when & then
				assertSoftly(softly -> {
					softly.assertThatCode(() -> reviewService.updateReview(1L, 1L, request))
							.doesNotThrowAnyException();
				});
			}
		}

		@Nested
		class Failure {
			@Test
			void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
				// given
				given(movieRepository.findById(any())).willReturn(Optional.empty());

				PutReviewRequest request = new PutReviewRequest("제목", "내용");

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, request))
							.isInstanceOf(MovieNotFoundException.class);
				});
			}

			@Test
			void 존재하지_않는_리뷰_ReviewNotFoundException_예외발생() {
				// given
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));
				given(reviewRepository.findById(any())).willReturn(Optional.empty());

				PutReviewRequest request = new PutReviewRequest("제목", "내용");

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, request))
							.isInstanceOf(ReviewNotFoundException.class);
				});
			}
		}
	}

	@Nested
	class DeleteReview {
		@Nested
		class Success {
			@Test
			void 리뷰_삭제_성공() {
				// given
				Movie movie = mock(Movie.class);
				Review review = mock(Review.class);

				given(movieRepository.findById(any())).willReturn(Optional.of(movie));
				given(reviewRepository.findById(any())).willReturn(Optional.of(review));

				// when & then
				assertSoftly(softly -> {
					softly.assertThatCode(() -> reviewService.deleteReview(1L, 1L))
							.doesNotThrowAnyException();
				});
			}
		}

		@Nested
		class Failure {
			@Test
			void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
				// given
				given(movieRepository.findById(any())).willReturn(Optional.empty());

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L))
							.isInstanceOf(MovieNotFoundException.class);
				});
			}

			@Test
			void 존재하지_않는_리뷰_ReviewNotFoundException_예외발생() {
				// given
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));
				given(reviewRepository.findById(any())).willReturn(Optional.empty());

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L))
							.isInstanceOf(ReviewNotFoundException.class);
				});
			}
		}
	}

	@Nested
	class FindReviewByMovie {

		@Nested
		class Success {

			@Test
			void 영화별_리뷰_조회() {
			    // given -- 테스트의 상태 설정
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));

				Member member = mock(Member.class);
				Review review1 = mock(Review.class);
				Review review2 = mock(Review.class);

				given(review1.getMovie()).willReturn(movie);
				given(review2.getMovie()).willReturn(movie);
				given(review1.getMember()).willReturn(member);
				given(review2.getMember()).willReturn(member);

				List<Review> reviewList = List.of(review1, review2);
				Slice<Review> reviews = new SliceImpl<>(reviewList);

				given(reviewRepository.findAllByMovieId(any(), any())).willReturn(reviews);

				Pageable pageable = PageRequest.of(0, 10);

			    // when -- 테스트하고자 하는 행동
				ReadReviewListResponse result = reviewService.findReviewByMovie(1L, pageable);

			    // then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result.reviews()).isNotNull();
					softly.assertThat(result.reviews().getSize()).isEqualTo(2);
				});
			}

			@Test
			void 리뷰_없을_경우_빈_목록_반환() {
				// given -- 테스트의 상태 설정
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));

				Slice<Review> reviews = new SliceImpl<>(List.of());
				given(reviewRepository.findAllByMovieId(any(), any())).willReturn(reviews);

				Pageable pageable = PageRequest.of(0, 10);

				// when -- 테스트하고자 하는 행동
				ReadReviewListResponse result = reviewService.findReviewByMovie(1L, pageable);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result.reviews()).isEmpty();
					softly.assertThat(result.reviews().getSize()).isEqualTo(0);
				});
			}
		}

		@Nested
		class Failure {

			@Test
			void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
			    // given -- 테스트의 상태 설정
				given(movieRepository.findById(any())).willReturn(Optional.empty());

			    // when -- 테스트하고자 하는 행동

			    // then -- 예상되는 변화 및 결과
				assertThrows(MovieNotFoundException.class, () -> {
					reviewService.findReviewByMovie(1L, PageRequest.of(0, 10));
				});
			}

		}
	}
  
}