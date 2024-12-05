package net.pointofviews.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewKeywordLink;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.*;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewMemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static net.pointofviews.common.exception.S3Exception.invalidTotalImageSize;
import static net.pointofviews.member.exception.MemberException.memberNotFound;
import static net.pointofviews.movie.exception.MovieException.movieNotFound;
import static net.pointofviews.review.exception.ReviewException.reviewNotFound;
import static net.pointofviews.review.exception.ReviewException.unauthorizedReviewDelete;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewMemberServiceImpl implements ReviewMemberService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewLikeCountRepository reviewLikeCountRepository;
    private final ReviewKeywordLinkRepository reviewKeywordLinkRepository;
    private final CommonCodeService commonCodeService;
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
        if (!request.keywords().isEmpty()) {
            request.keywords().forEach(keywordName -> {
                String keywordCode = commonCodeService.convertCommonCodeNameToCommonCode(
                        keywordName,
                        CodeGroupEnum.REVIEW_KEYWORD
                );

                ReviewKeywordLink reviewKeywordLink = ReviewKeywordLink.builder()
                        .review(review)
                        .reviewKeywordCode(keywordCode)
                        .build();
                reviewKeywordLinkRepository.save(reviewKeywordLink);
            });
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
    public void deleteReview(Long movieId, Long reviewId, Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        if (movieRepository.findById(movieId).isEmpty()) {
            throw movieNotFound(movieId);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> reviewNotFound(reviewId));

        if (!review.getMember().getId().equals(member.getId())) {
            throw unauthorizedReviewDelete();
        }

        // 이미지 삭제 로직
        deleteReviewImagesFolder(movieId, loginMember);

        review.delete(); // soft delete 처리
    }

    @Override
    public ReadReviewListResponse findReviewByMovie(Long movieId, Pageable pageable) {

        if (movieRepository.findById(movieId).isEmpty()) {
            throw movieNotFound(movieId);
        }

        Slice<ReadReviewResponse> reviews = reviewRepository.findReviewsWithLikesByMovieId(movieId, pageable);

        return new ReadReviewListResponse(reviews);
    }

    @Override
    public ReadReviewListResponse findAllReview() {
        return null;
    }

    @Override
    public ReadReviewDetailResponse findReviewDetail(Long reviewId) {

        Review review = reviewRepository.findReviewDetailById(reviewId)
                .orElseThrow(() -> reviewNotFound(reviewId));

        Long likeAmount = reviewLikeCountRepository.getReviewLikeCountByReviewId(reviewId);
        boolean isLiked = reviewLikeRepository.getIsLikedByReviewId(reviewId);
        List<String> keywords = reviewKeywordLinkRepository.findKeywordsByReviewId(reviewId);

        ReadReviewDetailResponse response = new ReadReviewDetailResponse(
                review.getTitle(),
                review.getContents(),
                review.getMember().getNickname(),
                review.getMember().getProfileImage(),
                review.getThumbnail(),
                review.getCreatedAt(),
                likeAmount,
                isLiked,
                review.isSpoiler(),
                keywords
        );

        return response;
    }

    @Override
    public ReadReviewListResponse findReviewByMember(Member loginMember, Pageable pageable) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        Slice<ReadReviewResponse> reviews = reviewRepository.findReviewsWithLikesByMemberId(member.getId(), pageable);

        return new ReadReviewListResponse(reviews);
    }

    @Override
    @Transactional
    public void updateReviewLike(Long movieId, Long reviewId, Member loginMember) {
        // 영화, 리뷰 존재 확인
        if (!movieRepository.existsById(movieId)) {
            throw movieNotFound(movieId);
        }

        if (!reviewRepository.existsById(reviewId)) {
            throw reviewNotFound(reviewId);
        }

        // redis에 좋아요 저장
    }

    @Override
    public CreateReviewImageListResponse saveReviewImages(List<MultipartFile> files, Long movieId, Member loginMember)
    {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        long totalSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > 10 * 1024 * 1024) {  // 총 파일 크기 10MB 제한
            throw invalidTotalImageSize();
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            s3Service.validateImageFile(file);

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = s3Service.createUniqueFileName(originalFileName);
            String filePath = String.format("users/%s/movie/%d/%s",
                    member.getId(),
                    movieId,
                    uniqueFileName);

            String imageUrl = s3Service.saveImage(file, filePath);

            imageUrls.add(imageUrl);
        }

        return new CreateReviewImageListResponse(imageUrls);
    }

    @Override
    @Transactional
    public void deleteReviewImagesFolder(Long movieId, Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        if (!movieRepository.existsById(movieId)) {
            throw movieNotFound(movieId);
        }

        String folderPath = String.format("users/%s/movie/%d",
                member.getId(),
                movieId);

        s3Service.deleteFolder(folderPath);
    }
}
