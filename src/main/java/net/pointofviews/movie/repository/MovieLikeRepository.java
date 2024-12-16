package net.pointofviews.movie.repository;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {

    @Query(value = """
        SELECT ml
          FROM MovieLike ml
         WHERE ml.movie = :movie
           AND ml.member = :member
    """)
    Optional<MovieLike> findByMovieAndMember(@Param("movie") Movie movie, @Param("member") Member member);
}
