package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;

import java.util.UUID;

public interface ClubMovieService {
    ReadClubMoviesListResponse readClubMovies(UUID clubId);
}
