package net.pointofviews.review.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import net.pointofviews.review.domain.ReviewLikeCount;

public interface ReviewLikeCountRepository extends CrudRepository<ReviewLikeCount, Long> {

	@Query(value = """
		SELECT rlc.reviewLikeCount
		  FROM ReviewLikeCount rlc
	  	 WHERE rlc.reviewId = :reviewId
	""")
	Long getReviewLikeCountByReviewId(@Param("reviewId") Long reviewId);

}
