package net.pointofviews.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.review.domain.ReviewKeywordLink;

public interface ReviewKeywordLinkRepository extends JpaRepository<ReviewKeywordLink, Long> {

	@Query(value = """
		SELECT cc.name
		  FROM ReviewKeywordLink rkl
		  JOIN CommonCode cc ON rkl.reviewKeywordCode = cc.code.code
		 WHERE rkl.review.id = :reviewId
		   AND cc.groupCode.groupCode = '020'
	""")
	List<String> findKeywordsByReviewId(@Param("reviewId") Long reviewId);

}