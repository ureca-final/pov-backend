package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.movie.dto.response.AdminSearchMovieListResponse;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import org.springframework.data.domain.Pageable;

public interface MovieSearchService {
    SearchMovieListResponse searchMovies(String query, Member loginMember, Pageable pageable);

    AdminSearchMovieListResponse adminSearchMovies(String query, Pageable pageable);

    ReadDetailMovieResponse readDetailMovie(Long movieId, MemberDetailsDto memberDetails);
}
