package net.pointofviews.review.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import net.pointofviews.common.exception.S3Exception;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.dto.response.ReadReviewDetailResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.exception.ReviewException;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.impl.ReviewMemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewMemberServiceTest {

	@InjectMocks
	private ReviewMemberServiceImpl reviewService;

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

	@Mock
	private S3Service s3Service;

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
							.isInstanceOf(MovieException.class);
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
							.isInstanceOf(MovieException.class);
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
							.isInstanceOf(ReviewException.class);
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
				given(review.getContents()).willReturn("""
					<p>리뷰 내용</p>
					<img src="https://s3-bucket.../image1.jpg" />
					<img src="https://s3-bucket.../image2.jpg" />
					""");

				given(s3Service.extractImageUrlsFromHtml(anyString()))
					.willReturn(List.of(
						"https://s3-bucket.../image1.jpg",
						"https://s3-bucket.../image2.jpg"
					));

				// when & then
				assertSoftly(softly -> {
					softly.assertThatCode(() -> reviewService.deleteReview(1L, 1L))
							.doesNotThrowAnyException();
					verify(s3Service, times(2)).deleteImage(anyString());
					verify(review, times(1)).delete();
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
							.isInstanceOf(MovieException.class);
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
							.isInstanceOf(ReviewException.class);
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

				ReadReviewResponse review1 = new ReadReviewResponse(
					1L,
					movie.getTitle(),
					"리뷰제목1",
					"리뷰내용1",
					"작성자1",
					"https://example.com/profileImage1.jpg",
					"https://example.com/thumbnail1.jpg",
					LocalDateTime.of(2024, 12, 25, 0, 0),
					10L,
					true,
					false
				);

				ReadReviewResponse review2 = new ReadReviewResponse(
					2L,
					movie.getTitle(),
					"리뷰제목2",
					"리뷰내용2",
					"작성자2",
					"https://example.com/profileImage2.jpg",
					"https://example.com/thumbnail2.jpg",
					LocalDateTime.of(2023, 12, 25, 0, 0),
					20L,
					false,
					false
				);

				List<ReadReviewResponse> reviewList = List.of(review1, review2);
				Slice<ReadReviewResponse> reviews = new SliceImpl<>(reviewList);

				given(reviewRepository.findReviewsWithLikesByMovieId(any(), any())).willReturn(reviews);

				Pageable pageable = PageRequest.of(0, 10);

			    // when -- 테스트하고자 하는 행동
				ReadReviewListResponse result = reviewService.findReviewByMovie(1L, pageable);

			    // then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result.reviews().getSize()).isEqualTo(2);
					softly.assertThat(result.reviews().getContent().get(0)).isEqualTo(review1);
					softly.assertThat(result.reviews().getContent().get(1)).isEqualTo(review2);
				});
			}

			@Test
			void 리뷰_없을_경우_빈_목록_반환() {
				// given -- 테스트의 상태 설정
				Movie movie = mock(Movie.class);
				given(movieRepository.findById(any())).willReturn(Optional.of(movie));

				Slice<ReadReviewResponse> reviews = new SliceImpl<>(List.of());
				given(reviewRepository.findReviewsWithLikesByMovieId(any(), any())).willReturn(reviews);

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
			void 존재하지_않는_영화_MovieException_movieNotFound_예외발생() {
			    // given -- 테스트의 상태 설정
				given(movieRepository.findById(any())).willReturn(Optional.empty());

			    // when -- 테스트하고자 하는 행동
				MovieException exception = assertThrows(MovieException.class, () ->
					reviewService.findReviewByMovie(-1L, PageRequest.of(0, 10))
				);

			    // then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
					softly.assertThat(exception.getMessage()).isEqualTo("영화(Id: -1)는 존재하지 않습니다.");
				});
			}
		}
	}

	@Nested
	class FindReviewDetail {

		@Nested
		class Success {

			@Test
			void 리뷰_상세_조회() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				Review review = mock(Review.class);

				given(review.getMember()).willReturn(member);

				given(reviewRepository.findReviewDetailById(any())).willReturn(Optional.of(review));
				given(reviewLikeRepository.getIsLikedByReviewId(any())).willReturn(true);
				given(reviewLikeCountRepository.getReviewLikeCountByReviewId(any())).willReturn(10L);
				given(reviewKeywordLinkRepository.findKeywordsByReviewId(any())).willReturn(List.of("흥미진진", "몰입감"));

				// when -- 테스트하고자 하는 행동
				ReadReviewDetailResponse result = reviewService.findReviewDetail(1L);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result).isNotNull();
					softly.assertThat(result.title()).isEqualTo(review.getTitle());
					softly.assertThat(result.contents()).isEqualTo(review.getContents());
					softly.assertThat(result.reviewer()).isEqualTo(member.getNickname());
					softly.assertThat(result.profileImage()).isEqualTo(member.getProfileImage());
					softly.assertThat(result.thumbnail()).isEqualTo(review.getThumbnail());
					softly.assertThat(result.likeAmount()).isEqualTo(10L);
					softly.assertThat(result.isLiked()).isTrue();
					softly.assertThat(result.keywords()).contains("흥미진진", "몰입감");
				});
			}
		}

		@Nested
		class Failure {

			@Test
			void 존재하지_않는_리뷰_ReviewException_reviewNotFound_예외발생() {
				// given -- 테스트의 상태 설정
				given(reviewRepository.findReviewDetailById(any())).willReturn(Optional.empty());

				// when -- 테스트하고자 하는 행동
				ReviewException exception = assertThrows(ReviewException.class, () ->
					reviewService.findReviewDetail(-1L)
				);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
					softly.assertThat(exception.getMessage()).isEqualTo("리뷰(Id: -1)는 존재하지 않습니다.");
				});
			}
		}
	}

	@Nested
	class SaveReviewImages {
		@Nested
		class Success {
			@Test
			void 이미지_업로드_성공() {
				// given
				MockMultipartFile file = new MockMultipartFile(
						"test-image",
						"test.jpg",
						MediaType.IMAGE_JPEG_VALUE,
						"test image content".getBytes()
				);
				given(s3Service.saveImage(any(), any()))
						.willReturn("https://s3-bucket.../test.jpg");

				// when
				CreateReviewImageListResponse response = reviewService.saveReviewImages(List.of(file));

				// then
				assertSoftly(softly -> {
					softly.assertThat(response.imageUrls()).hasSize(1);
					verify(s3Service, times(1)).saveImage(any(), any());
				});
			}
		}

		@Nested
		class Failure {
			@Test
			void 총_파일_크기_초과시_ImageException_invalidTotalImageSize_예외발생() {
				// given
				MockMultipartFile file1 = new MockMultipartFile(
						"image1",
						"test1.jpg",
						MediaType.IMAGE_JPEG_VALUE,
						new byte[6 * 1024 * 1024]  // 6MB
				);
				MockMultipartFile file2 = new MockMultipartFile(
						"image2",
						"test2.jpg",
						MediaType.IMAGE_JPEG_VALUE,
						new byte[5 * 1024 * 1024]  // 5MB
				);

				List<MultipartFile> files = List.of(file1, file2);  // 총 11MB

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.saveReviewImages(files))
							.isInstanceOf(S3Exception.class)
							.hasMessage("전체 파일 크기가 10MB를 초과합니다.");
				});
			}
		}
	}

	@Nested
	class DeleteReviewImages {
		@Nested
		class Success {
			@Test
			void 이미지_삭제_성공() {
				// given
				List<String> imageUrls = List.of(
						"https://s3-bucket.../image1.jpg",
						"https://s3-bucket.../image2.jpg"
				);

				// when & then
				assertSoftly(softly -> {
					softly.assertThatCode(() -> reviewService.deleteReviewImages(imageUrls))
							.doesNotThrowAnyException();
					verify(s3Service, times(2)).deleteImage(any());
				});
			}
		}

		@Nested
		class Failure {
			@Test
			void 빈_URL_리스트로_삭제시_ImageException_emptyImageUrls_예외발생() {
				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> reviewService.deleteReviewImages(List.of()))
							.isInstanceOf(S3Exception.class)
							.hasMessage("삭제할 이미지 URL이 없습니다.");
				});
			}
		}
	}

}