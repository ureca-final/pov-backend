package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.*;

import java.time.LocalDate;

public interface MovieApiSearchService {
    SearchMovieApiListResponse searchMovie(String searchTerm, int page);

    SearchFilteredMovieDetailResponse searchDetailsMovie(String movieId);

    SearchCreditApiResponse searchLimit5Credit(String movieId);

    SearchReleaseApiResponse searchReleaseDate(String movieId);

    SearchMovieDiscoverApiResponse searchDiscoverMovie(LocalDate start, LocalDate end, int page);

    SearchMovieTrendingApiResponse searchTrendingMovie(String timeWindow, int page);

    SearchMovieImageApiResponse searchImageMovie(String movieId);
}