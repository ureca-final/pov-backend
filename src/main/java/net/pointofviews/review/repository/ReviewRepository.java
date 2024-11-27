package net.pointofviews.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.pointofviews.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query(value = """
		SELECT r
		  FROM Review r
		  JOIN FETCH r.movie m
		 WHERE m.id = :movieId
		 ORDER BY r.createdAt DESC
	""")
	Slice<Review> findAllByMovieId(@Param("movieId") Long movieId, Pageable pageable);
}
