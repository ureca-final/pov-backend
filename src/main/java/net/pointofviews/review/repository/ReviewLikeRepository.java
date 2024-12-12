package net.pointofviews.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.review.domain.ReviewLike;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	@Query(value = """
		SELECT rl.isLiked
		  FROM ReviewLike rl
		 WHERE rl.review.id = :reviewId
	""")
	Optional<Boolean> getIsLikedByReviewId(@Param("reviewId") Long reviewId);
}
