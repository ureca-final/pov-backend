package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.SearchMovieApiListResponse;

public interface MovieApiSearchService {
    SearchMovieApiListResponse searchMovie(String searchTerm, int page);
}
