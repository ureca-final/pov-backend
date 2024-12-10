package net.pointofviews.club.repository;

import net.pointofviews.club.dto.response.FindBasicClubInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query("""
       SELECT new net.pointofviews.club.dto.response.FindBasicClubInfo(
           c.name,
           c.description,
           c.clubImage,
           c.isPublic,
           COUNT(DISTINCT mc.id),
           COUNT(DISTINCT cm.id)
       )
       FROM Club c
       LEFT JOIN c.memberClubs mc
       LEFT JOIN c.clubMovies cm
       WHERE c.id = :clubId
       """)
    Optional<FindBasicClubInfo> findBasicClubInfoById(@Param("clubId") UUID clubId);


    /**
     * 검색
     */
    @Query(value = """
    SELECT DISTINCT BIN_TO_UUID(c.id) AS clubId, c.name AS clubName, c.description AS clubDescription,
           COUNT(DISTINCT mc.id) AS participantCount,
           c.max_participants AS maxParticipant,
           COUNT(DISTINCT cm.id) AS clubMovieCount,
           COALESCE(GROUP_CONCAT(DISTINCT cfg.genre_code), '') AS clubFavorGenres
    FROM club c
    LEFT JOIN member_club mc ON mc.club_id = c.id
    LEFT JOIN club_movie cm ON cm.club_id = c.id
    LEFT JOIN club_favor_genre cfg ON cfg.club_id = c.id
    LEFT JOIN member m ON m.id = mc.member_id
    WHERE MATCH(c.name) AGAINST(:query IN NATURAL LANGUAGE MODE)
       OR EXISTS (
           SELECT 1
           FROM member_club mc2
           JOIN member m2 ON m2.id = mc2.member_id
           WHERE mc2.club_id = c.id
             AND MATCH(m2.nickname) AGAINST(:query IN NATURAL LANGUAGE MODE)
             AND mc2.is_leader = TRUE
       )
    GROUP BY c.id
    """,
            nativeQuery = true)
    Slice<Object[]> searchClubsByTitleOrNickname(@Param("query") String query, Pageable pageable);
}
