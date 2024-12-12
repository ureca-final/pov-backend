package net.pointofviews.movie.repository;

import net.pointofviews.movie.domain.MovieLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieLikeCountRepository extends JpaRepository<MovieLikeCount, Long> {
}
