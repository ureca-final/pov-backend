package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.dto.SearchMovieCriteria;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import net.pointofviews.movie.service.MovieContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController implements MovieSpecification {

    private final MovieContentService movieContentService;

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(SearchMovieCriteria criteria) {
        return null;
    }

    @Override
    @GetMapping("/{movieId}")
    public ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(@PathVariable Long movieId) {
        return null;
    }
}