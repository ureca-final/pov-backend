package net.pointofviews.movie.repository;

import net.pointofviews.movie.domain.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
}
