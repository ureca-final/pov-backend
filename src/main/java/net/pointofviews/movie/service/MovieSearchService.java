package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.*;
import org.springframework.data.domain.Pageable;

public interface MovieSearchService {
    SearchMovieListResponse searchMovies(String query, Pageable pageable);
}
