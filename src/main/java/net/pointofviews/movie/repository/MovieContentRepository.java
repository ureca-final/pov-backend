package net.pointofviews.movie.repository;

import net.pointofviews.movie.domain.MovieContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieContentRepository extends JpaRepository<MovieContent, Long> {
    List<MovieContent> findAllByMovieId(Long movieId);
}
