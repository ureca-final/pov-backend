package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MovieSearchService {
    Slice<SearchMovieResponse> searchMovies(String query, Pageable pageable);
}
