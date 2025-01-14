package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.domain.ClubMovie;
import net.pointofviews.club.dto.response.ReadClubMovieResponse;
import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.club.repository.ClubMoviesRepository;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.service.ClubMovieService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static net.pointofviews.movie.exception.MovieException.movieAlreadyInBookmark;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubMovieServiceImpl implements ClubMovieService {

    private final ClubMoviesRepository clubMoviesRepository;
    private final ClubRepository clubRepository;
    private final MovieRepository movieRepository;

    @Override
    public ReadClubMoviesListResponse readClubMovies(UUID clubId, UUID memberId, Pageable pageable) {
        Slice<ReadClubMovieResponse> movies = clubMoviesRepository.findMovieDetailsByClubId(clubId, memberId, pageable);
        return new ReadClubMoviesListResponse(movies);
    }

    @Override
    @Transactional
    public void saveMovieToMyClub(Long movieId, UUID clubId) {
        // 영화가 존재하는지 확인
        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> MovieException.movieNotFound(movieId));

        // 클럽이 존재하는지 확인
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> ClubException.clubNotFound(clubId));

        // 이미 영화가 클럽에 추가되었는지 확인
        boolean isAlreadyAdded = clubMoviesRepository.existsByMovieIdAndClubId(movieId, clubId);
        if (isAlreadyAdded) {
            throw movieAlreadyInBookmark(movieId);
        }

        ClubMovie clubMovie = ClubMovie.builder()
                .movie(movie)
                .club(club)
                .build();

        clubMoviesRepository.save(clubMovie);
    }
}
