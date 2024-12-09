package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.dto.response.ReadClubMovieResponse;
import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;
import net.pointofviews.club.repository.ClubMoviesRepository;
import net.pointofviews.club.service.ClubMovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubMovieServiceImpl implements ClubMovieService {

    private final ClubMoviesRepository clubMoviesRepository;

    @Override
    public ReadClubMoviesListResponse readClubMovies(UUID clubId) {
        List<ReadClubMovieResponse> movies = clubMoviesRepository.findMovieDetailsByClubId(clubId);
        return new ReadClubMoviesListResponse(movies);
    }
}
