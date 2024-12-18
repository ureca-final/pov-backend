package net.pointofviews.review.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewDetailResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ReviewMemberService {

    void saveReview(Long movieId, CreateReviewRequest request, Member loginMember);

    ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request);

    void updateReview(Long movieId, Long reviewId, PutReviewRequest request, Member loginMember);

    void deleteReview(Long movieId, Long reviewId, Member loginMember);

    ReadReviewListResponse findAllReview(Pageable pageable, UUID memberDetailsDto);

    ReadReviewListResponse findReviewByMovie(UUID memberId, Long movieId, Pageable pageable);

    ReadReviewDetailResponse findReviewDetail(UUID memberId, Long reviewId);

    ReadReviewListResponse findReviewByMember(Member loginMember, Pageable pageable);

    void updateReviewLike(Long movieId, Long reviewId, Member loginMember);

    void updateReviewDisLike(Long movieId, Long reviewId, Member loginMember);

    CreateReviewImageListResponse saveReviewImages(List<MultipartFile> files, Long movieId, Member loginMember);

    void deleteReviewImagesFolder(Long movieId, Member loginMember);
}
