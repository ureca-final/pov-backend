package net.pointofviews.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import net.pointofviews.review.domain.ReviewKeywordLink;

public interface ReviewKeywordLinkRepository extends JpaRepository<ReviewKeywordLink, Long> {
}