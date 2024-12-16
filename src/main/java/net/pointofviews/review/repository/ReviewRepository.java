package net.pointofviews.review.repository;

import net.pointofviews.review.domain.Review;
import net.pointofviews.review.dto.ReviewDetailsWithLikeCountDto;
import net.pointofviews.review.dto.ReviewPreferenceCountDto;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            	SELECT new net.pointofviews.review.dto.response.ReadReviewResponse(
            			r.id,
                        mv.id,
            			mv.title,
            			r.title,
            			r.contents,
            			m.nickname,
            			m.profileImage,
            			mv.poster,
            			r.createdAt,
            			COALESCE((SELECT rlc.reviewLikeCount FROM ReviewLikeCount rlc WHERE rlc.review.id = r.id), 0),
            			CASE WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review.id = r.id AND rl.isLiked = true) THEN true ELSE false END,
            			r.isSpoiler
            	 )
            	 FROM Review r
            	 JOIN r.member m
            	 JOIN r.movie mv
            	 WHERE mv.id = :movieId AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Slice<ReadReviewResponse> findReviewsWithLikesByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query(value = """
            	SELECT new net.pointofviews.review.dto.response.ReadReviewResponse(
            			r.id,
                        mv.id,
            			mv.title,
            			r.title,
            			r.contents,
            			m.nickname,
            			m.profileImage,
            			mv.poster,
            			r.createdAt,
            			COALESCE((SELECT rlc.reviewLikeCount FROM ReviewLikeCount rlc WHERE rlc.review.id = r.id), 0),
            			CASE WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review.id = r.id AND rl.isLiked = true) THEN true ELSE false END,
            			r.isSpoiler
            	 )
            	 FROM Review r
            	 JOIN r.member m
            	 JOIN r.movie mv
            	 WHERE m.id = :memberId AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Slice<ReadReviewResponse> findReviewsWithLikesByMemberId(@Param("memberId") UUID memberId, Pageable pageable);

    @Query(value = """
            	SELECT r
            	  FROM Review r
            	  JOIN FETCH r.member m
            	  JOIN FETCH r.movie mv
            	 WHERE r.id = :reviewId
            """)
    Optional<Review> findReviewDetailById(@Param("reviewId") Long reviewId);

    @Query(value = """
            	SELECT new net.pointofviews.review.dto.response.ReadReviewResponse(
            			r.id,
            			mv.id,
            			mv.title,
            			r.title,
            			r.contents,
            			m.nickname,
            			m.profileImage,
            			mv.poster,
            			r.createdAt,
            			COALESCE((SELECT rlc.reviewLikeCount FROM ReviewLikeCount rlc WHERE rlc.review.id = r.id), 0),
            			CASE WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review.id = r.id AND rl.isLiked = true) THEN true ELSE false END,
            			r.isSpoiler
            	 )
            	 FROM Review r
            	 LEFT JOIN r.member m
            	 LEFT JOIN r.movie mv
            	 WHERE r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Slice<ReadReviewResponse> findAllSliced(Pageable pageable);

    @Query("""
                SELECT new net.pointofviews.review.dto.ReviewPreferenceCountDto(
                    COALESCE(SUM(CASE WHEN r.preference = 'GOOD' THEN 1 ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN r.preference = 'BAD' THEN 1 ELSE 0 END), 0)
                )
                FROM Review r
                WHERE r.movie.id = :movieId
            """)
    List<ReviewPreferenceCountDto> countReviewPreferenceByMovieId(Long movieId);

    @Query("""
                SELECT new net.pointofviews.review.dto.ReviewDetailsWithLikeCountDto(
                    r,
                    rlc.reviewLikeCount
                )
                FROM Review r
                LEFT JOIN ReviewLikeCount rlc on r.id = rlc.review.id
                WHERE r.movie.id = :movieId
            """)
    List<ReviewDetailsWithLikeCountDto> findTop3ByMovieIdOrderByReviewLikeCountDesc(@Param("movieId") Long movieId, Pageable pageable);

    @Query(value = """
            SELECT
                r.id,
                mv.id,
                mv.title,
                r.title,
                r.contents,
                m.nickname,
                m.profile_image,
                mv.poster,
                r.created_at,
                COALESCE(rlc.review_like_count, 0),
                r.is_spoiler
            FROM review r
            LEFT JOIN member m ON r.member_id = m.id
            LEFT JOIN movie mv ON mv.id = r.movie_id
            LEFT JOIN review_like_count rlc ON rlc.review_id = r.id
            WHERE MATCH(mv.title) AGAINST(:query IN NATURAL LANGUAGE MODE)
            ORDER BY r.created_at DESC
            """,
            nativeQuery = true)
    Page<Object[]> searchReviewByMovieTitle(@Param("query") String query, Pageable pageable);
}
