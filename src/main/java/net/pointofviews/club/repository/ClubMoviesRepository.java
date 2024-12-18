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
                CASE WHEN :memberId IS NOT NULL AND EXISTS (SELECT 1 FROM MovieLike ml WHERE ml.movie.id = m.id AND ml.member.id = :memberId AND  ml.isLiked = true) THEN true ELSE false END,
                COALESCE((SELECT mlc.likeCount FROM MovieLikeCount mlc WHERE mlc.movie.id = m.id), 0),
                COUNT(r.id)
            )
            FROM ClubMovie cm
            JOIN cm.movie m
            LEFT JOIN m.reviews r
            WHERE cm.club.id = :clubId
            GROUP BY m.id, m.title, m.poster, m.released
            """)
    Slice<ReadClubMovieResponse> findMovieDetailsByClubId(@Param("clubId") UUID clubId, @Param("memberId") UUID memberId, Pageable pageable);

    boolean existsByMovieIdAndClubId(Long movieId, UUID clubId);

    Long countByClub(Club privateClub);
}
