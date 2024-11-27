package net.pointofviews.movie.repository;

import net.pointofviews.movie.domain.MovieContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieContentRepository extends JpaRepository<MovieContent, Long> {
}
