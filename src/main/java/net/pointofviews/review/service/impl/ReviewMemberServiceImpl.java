package net.pointofviews.review.service.impl;

import static net.pointofviews.movie.exception.MovieException.*;
import static net.pointofviews.review.exception.ReviewException.*;

import net.pointofviews.common.service.S3Service;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.exception.ImageException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewKeywordLink;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewMemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewMemberServiceImpl implements ReviewMemberService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewLikeCountRepository reviewLikeCountRepository;
	private final ReviewKeywordLinkRepository reviewKeywordLinkRepository;
	private final S3Service s3Service;

	@Override
	@Transactional
	public void saveReview(Long movieId, CreateReviewRequest request) {

		 Movie movie = movieRepository.findById(movieId)
            		.orElseThrow(() -> movieNotFound(movieId));

		// 리뷰 생성 및 저장
		Review review = Review.builder()
				.title(request.title())
				.contents(request.contents())
				.preference(request.preference())
				.isSpoiler(request.spoiler())
				.movie(movie)
				.build();

		reviewRepository.save(review);

		// 키워드 저장
		if (request.keywords() != null && !request.keywords().isEmpty()) {
			for (String keyword : request.keywords()) {
				ReviewKeywordLink keywordLink = ReviewKeywordLink.builder()
						.review(review)
						.reviewKeywordCode(keyword)
						.build();

				reviewKeywordLinkRepository.save(keywordLink);
			}
		}
	}

	@Override
	public ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request) {
		return null;
	}

	@Override
	@Transactional
	public void updateReview(Long movieId, Long reviewId, PutReviewRequest request) {
		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> reviewNotFound(reviewId));

		review.update(request.title(), request.contents());
	}

	@Override
	@Transactional
	public void deleteReview(Long movieId, Long reviewId) {
		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> reviewNotFound(reviewId));

		// 이미지 삭제 로직
		List<String> imageUrls = extractImageUrlsFromHtml(review.getContents());
		deleteReviewImages(imageUrls);


		review.delete(); // soft delete 처리
	}

	@Override
	public ReadReviewListResponse findReviewByMovie(Long movieId, Pageable pageable) {

		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Slice<ReadReviewResponse> reviews = reviewRepository.findAllWithLikesByMovieId(movieId, pageable);

		return new ReadReviewListResponse(reviews);
	}

	@Override
	public ReadReviewListResponse findAllReview() {
		return null;
	}

	@Override
	public ReadReviewResponse findReviewDetail(Long reviewId) {

		Review review = reviewRepository.findReviewDetailById(reviewId)
			.orElseThrow(() -> reviewNotFound(reviewId));

		Long likeAmount = reviewLikeCountRepository.getReviewLikeCountByReviewId(reviewId);
		boolean isLiked = reviewLikeRepository.getIsLikedByReviewId(reviewId);

		ReadReviewResponse response = new ReadReviewResponse(
			review.getMovie().getTitle(),
			review.getTitle(),
			review.getContents(),
			review.getMember().getNickname(),
			review.getThumbnail(),
			review.getCreatedAt(),
			likeAmount,
			isLiked
		);

		return response;
	}

	@Override
	public void updateReviewLike(Long reviewId, Long likedId) {

	}

	@Override
	public CreateReviewImageListResponse saveReviewImages(List<MultipartFile> files) {
		List<String> imageUrls = new ArrayList<>();

		for (MultipartFile file : files) {
			validateImageFile(file);

			String originalFilename = file.getOriginalFilename();
			if (originalFilename != null && !originalFilename.isEmpty()) {
				String uniqueFileName = createUniqueFileName(originalFilename);
				String filePath = "reviews/" + uniqueFileName;

				String imageUrl = s3Service.saveImage(file, filePath);
				imageUrls.add(imageUrl);
			}
		}

		return new CreateReviewImageListResponse(imageUrls);
	}

	@Override
	@Transactional
	public void deleteReviewImages(List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			throw ImageException.emptyImageUrls();
		}

		for (String imageUrl : imageUrls) {
			s3Service.deleteImage(imageUrl);
		}
	}

	private void validateImageFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw ImageException.emptyImage();
		}

		if (file.getSize() > 2 * 1024 * 1024) {
			throw ImageException.invalidImageSize();
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw ImageException.invalidImageFormat();
		}

		// 이미지 확장자 검증 추가
		String filename = file.getOriginalFilename();
		if (filename != null && !isImageFile(filename)) {
			throw ImageException.invalidImageFormat();
		}
	}

	private String createUniqueFileName(String originalFilename) {
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
		String uniquePrefix = UUID.randomUUID().toString();
		return baseName + "_" + uniquePrefix + extension;
	}

	private List<String> extractImageUrlsFromHtml(String html) {
		List<String> imageUrls = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(html);
			Elements imgTags = doc.select("img[src]");

			for (Element img : imgTags) {
				String imageUrl = img.attr("src");
				if (imageUrl.contains("s3")) {
					imageUrls.add(imageUrl);
				}
			}
			return imageUrls;
		} catch (Exception e) {
			throw ImageException.failedToParseHtml(e.getMessage());
		}
	}

	private boolean isImageFile(String filename) {
		String extension = filename.toLowerCase();
		return extension.endsWith(".jpg") ||
				extension.endsWith(".jpeg") ||
				extension.endsWith(".png");
	}
}
