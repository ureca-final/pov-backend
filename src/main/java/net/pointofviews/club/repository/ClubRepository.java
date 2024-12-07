package net.pointofviews.club.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.pointofviews.club.domain.Club;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {
    Optional<Club> findById(UUID clubId);

    @Query("""
    SELECT c
    FROM Club c
    LEFT JOIN FETCH c.memberClubs mc
    LEFT JOIN FETCH mc.member m
    WHERE c.id = :clubId
""")
    Optional<Club> findByIdWithMemberClubs(@Param("clubId") UUID clubId);


    @Query("""
           SELECT c.id AS clubId,
                  c.name AS clubName,
                  c.description AS clubDescription,
                  c.maxParticipants AS maxParticipant,
                  COUNT(DISTINCT mc.id) AS participantCount,
                  COUNT(DISTINCT cm.id) AS movieCount,
                  GROUP_CONCAT(DISTINCT cfg.genreCode) AS genreCodes
           FROM Club c
           LEFT JOIN c.memberClubs mc
           LEFT JOIN c.clubMovies cm
           LEFT JOIN c.clubFavorGenres cfg
           WHERE c.isPublic = true
           GROUP BY c.id
           """)
    List<Object[]> findAllPublicClubs();
}
