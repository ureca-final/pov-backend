package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.SearchCreditApiResponse;
import net.pointofviews.movie.dto.response.SearchMovieApiListResponse;
import net.pointofviews.movie.dto.response.SearchMovieDetailApiResponse;

public interface MovieApiSearchService {
    SearchMovieApiListResponse searchMovie(String searchTerm, int page);

    SearchMovieDetailApiResponse searchDetailsMovie(String movieId);

    SearchCreditApiResponse searchCredit(String movieId);
}
