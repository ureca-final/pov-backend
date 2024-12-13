package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.request.PutMovieRequest;

public interface MovieService {
    void saveMovie(CreateMovieRequest request);

    void deleteMovie(Long movieId);

    void updateMovie(Long movieId, PutMovieRequest request);
}
