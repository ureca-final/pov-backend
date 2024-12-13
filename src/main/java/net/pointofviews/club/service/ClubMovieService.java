package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClubMovieService {
    ReadClubMoviesListResponse readClubMovies(UUID clubId, Pageable pageable);

    void saveMovieToMyClub(Long movieId, UUID clubId);
}
