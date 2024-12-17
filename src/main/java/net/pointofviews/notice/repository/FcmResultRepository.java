package net.pointofviews.notice.repository;

import net.pointofviews.notice.domain.FcmResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmResultRepository extends JpaRepository<FcmResult, Long> {
}