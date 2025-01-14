package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClubMovieService {
    ReadClubMoviesListResponse readClubMovies(UUID clubId, UUID memberId, Pageable pageable);

    void saveMovieToMyClub(Long movieId, UUID clubId);
}
