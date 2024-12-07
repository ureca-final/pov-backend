package net.pointofviews.club.repository;

import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.ClubFavorGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubFavorGenreRepository extends JpaRepository<ClubFavorGenre, Long> {
    List<ClubFavorGenre> findAllByClub(Club club);

    void deleteAllByClub(Club club);
}
