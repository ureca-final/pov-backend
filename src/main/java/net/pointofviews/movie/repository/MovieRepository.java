package net.pointofviews.movie.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import net.pointofviews.movie.domain.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * 검색
     */
    @Query(value = """
            
            SELECT DISTINCT m.id, m.title, m.poster, m.released AS released,
            COALESCE(mlc.like_count, 0) AS likeCount,
                            (SELECT COUNT(*) FROM review r WHERE r.movie_id = m.id) AS reviewCount
            FROM movie m
            LEFT JOIN movie_like_count mlc ON mlc.movie_id = m.id
            WHERE MATCH(m.title) AGAINST(:query IN NATURAL LANGUAGE MODE)
            OR EXISTS (
                    SELECT 1
                            FROM people p
                            JOIN movie_cast mc ON mc.people_id = p.id
                            WHERE MATCH(p.name) AGAINST(:query IN NATURAL LANGUAGE MODE)
                            AND mc.movie_id = m.id
            )
            OR EXISTS (
                    SELECT 1
                            FROM people p
                            JOIN movie_crew mcr ON mcr.people_id = p.id
                            WHERE MATCH(p.name) AGAINST(:query IN NATURAL LANGUAGE MODE)
                            AND mcr.movie_id = m.id
            )
            """,
            nativeQuery = true)
    Slice<Object[]> searchMoviesByTitleOrPeople(@Param("query") String query, Pageable pageable);
    boolean existsByTmdbId(Integer title);
}
