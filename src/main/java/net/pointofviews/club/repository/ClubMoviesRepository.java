package net.pointofviews.club.repository;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.ClubMovie;
import net.pointofviews.club.dto.response.ReadClubMovieResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ClubMoviesRepository extends JpaRepository<ClubMovie, Long> {
    @Query("""
            SELECT new net.pointofviews.club.dto.response.ReadClubMovieResponse(
                m.title,
                m.poster,
                m.released,
                COALESCE(mlc.likeCount, 0) AS movieLikeCount,
                COUNT(r.id) AS movieReviewCount
            )
            FROM ClubMovie cm
            JOIN cm.movie m
            LEFT JOIN m.reviews r
            LEFT JOIN MovieLikeCount mlc ON mlc.movie.id = m.id
            WHERE cm.club.id = :clubId
            GROUP BY m.id, m.title, m.poster, m.released, mlc.likeCount
            """)
    Slice<ReadClubMovieResponse> findMovieDetailsByClubId(@Param("clubId") UUID clubId, Pageable pageable);

    boolean existsByMovieIdAndClubId(Long movieId, UUID clubId);

    Long countByClub(Club privateClub);
}
