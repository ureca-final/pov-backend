package net.pointofviews.movie.repository;

import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.dto.response.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
            SELECT
                m.id,
                m.title,
                m.poster,
                m.released,
                CASE WHEN :memberId IS NOT NULL AND EXISTS (SELECT 1 FROM MovieLike ml WHERE ml.movie.id = m.id AND ml.member.id = :memberId AND ml.isLiked = true) THEN true ELSE false END,
                COALESCE((SELECT mlc.likeCount FROM MovieLikeCount mlc WHERE mlc.movie.id = m.id), 0),
                COUNT(CASE WHEN r IS NOT NULL AND r.disabled = false THEN r.id ELSE NULL END)
            FROM Movie m
            LEFT JOIN m.reviews r
            GROUP BY m.id, m.title, m.poster, m.released
            ORDER BY m.released DESC
            """)
    Slice<Object[]> findAllMovies(@Param("memberId") UUID memberId, Pageable pageable);


    /**
     * 검색
     */
    @Query(value = """
    SELECT m.id AS id,
           m.title AS title,
           m.poster AS poster,
           m.released AS released,
           CASE WHEN :memberId IS NOT NULL AND EXISTS (
               SELECT 1 FROM movie_like ml
               WHERE ml.movie_id = m.id
                 AND ml.member_id = :memberId
                 AND ml.is_liked = true
           ) THEN true ELSE false END AS isLiked,
           COALESCE((
               SELECT mlc.like_count
               FROM movie_like_count mlc
               WHERE mlc.movie_id = m.id
           ), 0) AS movieLikeCount,
           (SELECT COUNT(*)
            FROM review r
            WHERE r.movie_id = m.id AND r.disabled = false) AS movieReviewCount
    FROM movie m
    WHERE MATCH(m.title) AGAINST(:query IN BOOLEAN MODE)
       OR EXISTS (
           SELECT 1
           FROM people p
           JOIN movie_cast mc ON mc.people_id = p.id
           WHERE MATCH(p.name) AGAINST(:query IN BOOLEAN MODE)
             AND mc.movie_id = m.id
       )
       OR EXISTS (
           SELECT 1
           FROM people p
           JOIN movie_crew mcr ON mcr.people_id = p.id
           WHERE MATCH(p.name) AGAINST(:query IN BOOLEAN MODE)
             AND mcr.movie_id = m.id
       )
    """,
            nativeQuery = true)
    Slice<Object[]> searchMoviesByTitleOrPeople(@Param("query") String query, @Param("memberId") UUID memberId, Pageable pageable);

    boolean existsByTmdbId(Integer id);


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

    @Query("""
            SELECT new net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse(
                m.title,
                m.poster,
                m.released,
                CASE WHEN :memberId IS NOT NULL AND EXISTS (SELECT 1 FROM MovieLike ml WHERE ml.movie.id = m.id AND ml.member.id = :memberId AND  ml.isLiked = true) THEN true ELSE false END,
                COALESCE((SELECT mlc.likeCount FROM MovieLikeCount mlc WHERE mlc.movie.id = m.id), 0),
                COUNT(r.id)
            )
            FROM Movie m
            LEFT JOIN m.reviews r
            WHERE m.id IN :movieIds
            GROUP BY m.id, m.title, m.poster, m.released
            """)
    List<ReadUserCurationMovieResponse> findUserCurationMoviesByIds(@Param("movieIds") Set<Long> movieIds, @Param("memberId") UUID memberId);

    @EntityGraph(attributePaths = {"genres", "countries.country", "crews.people", "casts.people"})
    @Query("SELECT DISTINCT m FROM Movie m WHERE m.id = :movieId")
    Optional<Movie> findMovieWithDetailsById(Long movieId);

    Optional<Movie> findMovieByTmdbId(Integer tmdbId);

    @Query("""
        SELECT new net.pointofviews.movie.dto.response.MovieTrendingResponse(
            m.id,
            m.title,
            m.poster,
            m.released,
            CASE WHEN :memberId IS NOT NULL AND EXISTS (
                SELECT 1 FROM MovieLike ml WHERE ml.movie.id = m.id AND ml.member.id = :memberId AND ml.isLiked = true
            ) THEN true ELSE false END,
            COALESCE((SELECT mlc.likeCount FROM MovieLikeCount mlc WHERE mlc.movie.id = m.id), 0),
            COALESCE(COUNT(r.id), 0)
        )
        FROM Movie m
        LEFT JOIN m.reviews r
        WHERE m.id IN :trendingMovieId
        GROUP BY m.id, m.title, m.poster, m.released
        ORDER BY m.released DESC
        """)
    List<MovieTrendingResponse> findAllTrendingMovie(
            @Param("trendingMovieId") List<Long> trendingMovieId,
            @Param("memberId") UUID memberId
    );
}
