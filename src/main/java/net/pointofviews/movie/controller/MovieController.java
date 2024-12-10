package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.controller.specification.MovieSpecification;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import net.pointofviews.movie.dto.response.SearchMovieResponse;
import net.pointofviews.movie.service.MovieSearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController implements MovieSpecification {

    private final MovieSearchService movieSearchService;

    @Override
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(@RequestParam String query,
                                                                                 Pageable pageable) {
        SearchMovieListResponse response = movieSearchService.searchMovies(query, pageable);
        return BaseResponse.ok("영화가 성공적으로 검색되었습니다.", response);
    }

    @Override
    @GetMapping("/{movieId}")
    public ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(@PathVariable Long movieId) {
        return null;
    }


}