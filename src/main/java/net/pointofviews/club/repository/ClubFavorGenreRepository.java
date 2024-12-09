package net.pointofviews.club.repository;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.ClubFavorGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClubFavorGenreRepository extends JpaRepository<ClubFavorGenre, Long> {
    List<ClubFavorGenre> findAllByClub(Club club);

    void deleteAllByClub(Club club);

    @Query("""
       SELECT cfg.genreCode
       FROM ClubFavorGenre cfg
       WHERE cfg.club.id = :clubId
       """)
    List<String> findGenresByClubId(@Param("clubId") UUID clubId);

}
