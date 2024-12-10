package net.pointofviews.movie.repository;

import net.pointofviews.movie.dto.response.AdminSearchMovieResponse;
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


    @Query(value = """
    SELECT DISTINCT m.id AS id,
           m.title AS title,
           m.released AS released
    FROM movie m
             LEFT JOIN movie_cast mc ON mc.movie_id = m.id
             LEFT JOIN movie_crew mcr ON mcr.movie_id = m.id
             LEFT JOIN people p_cast ON p_cast.id = mc.people_id
             LEFT JOIN people p_crew ON p_crew.id = mcr.people_id
             LEFT JOIN movie_genre mg ON mg.movie_id = m.id
             LEFT JOIN common_code cc ON cc.code = mg.genre_code
    WHERE (:query IS NULL OR m.title LIKE CONCAT('%', :query, '%'))
       OR (:query IS NULL OR p_cast.name LIKE CONCAT('%', :query, '%'))
       OR (:query IS NULL OR p_crew.name LIKE CONCAT('%', :query, '%'))
       OR (:query IS NULL OR CAST(m.released AS CHAR) LIKE CONCAT('%', :query, '%'))
       OR (:query IS NULL OR cc.common_code_description LIKE CONCAT('%', :query, '%'))
    """, nativeQuery = true)
    Slice<Object[]> adminSearchMovies(@Param("query") String query, Pageable pageable);
}
