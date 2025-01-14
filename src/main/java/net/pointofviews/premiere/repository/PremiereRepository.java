package net.pointofviews.premiere.repository;

import net.pointofviews.premiere.domain.Premiere;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiereRepository extends JpaRepository<Premiere, Long> {
}
