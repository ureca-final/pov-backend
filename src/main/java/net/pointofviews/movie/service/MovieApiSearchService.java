package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.*;

import java.time.LocalDate;

public interface MovieApiSearchService {
    SearchMovieApiListResponse searchMovie(String searchTerm, int page);

    SearchFilteredMovieDetailResponse searchDetailsMovie(String movieId);

    SearchCreditApiResponse searchLimit10Credit(String movieId);

    SearchReleaseApiResponse searchReleaseDate(String movieId);

    SearchMovieDiscoverApiResponse searchDiscoverMovie(LocalDate start, LocalDate end, int page);
}
