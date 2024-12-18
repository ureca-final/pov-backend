package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.response.AdminSearchMovieListResponse;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MovieSearchService {
    SearchMovieListResponse searchMovies(String query, UUID memberId, Pageable pageable);

    AdminSearchMovieListResponse adminSearchMovies(String query, Pageable pageable);

    ReadDetailMovieResponse readDetailMovie(Long movieId, UUID memberId);
}
