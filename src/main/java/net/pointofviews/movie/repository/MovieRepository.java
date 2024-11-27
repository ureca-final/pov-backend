package net.pointofviews.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.pointofviews.movie.domain.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
