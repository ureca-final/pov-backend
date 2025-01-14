package net.pointofviews.review.repository;

import net.pointofviews.member.domain.Member;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	@Query(value = """
		SELECT rl.isLiked
		  FROM ReviewLike rl
		 WHERE rl.review.id = :reviewId AND rl.member.id = :memberId
	""")
	Optional<Boolean> getIsLikedByReviewId(@Param("memberId") UUID memberId, @Param("reviewId") Long reviewId);

	@Query(value = """
        SELECT rl
          FROM ReviewLike rl
         WHERE rl.review = :review
           AND rl.member = :member
    """)
	Optional<ReviewLike> findByReviewAndMember(@Param("review") Review review, @Param("member") Member member);
}
