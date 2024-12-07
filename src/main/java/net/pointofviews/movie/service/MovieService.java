package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.request.CreateMovieRequest;

public interface MovieService {
    void saveMovie(CreateMovieRequest request);

    void deleteMovie(Long movieId);
}
