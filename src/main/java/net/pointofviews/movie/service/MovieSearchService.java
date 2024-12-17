package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.response.*;
import org.springframework.data.domain.Pageable;

public interface MovieSearchService {
    SearchMovieListResponse searchMovies(String query, Member loginMember, Pageable pageable);

    AdminSearchMovieListResponse adminSearchMovies(String query, Pageable pageable);

    ReadDetailMovieResponse readDetailMovie(Long movieId);
}
