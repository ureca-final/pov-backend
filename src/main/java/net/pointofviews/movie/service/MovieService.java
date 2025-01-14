package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.request.PutMovieRequest;
import net.pointofviews.movie.dto.response.MovieListResponse;
import net.pointofviews.movie.dto.response.MovieTrendingListResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MovieService {
    void saveMovie(CreateMovieRequest request);

    void deleteMovie(Long movieId);

    void updateMovie(Long movieId, PutMovieRequest request);

    MovieListResponse readMovies(UUID memberId, Pageable pageable);

    MovieTrendingListResponse getTrendingMovies(UUID memberId);
}
