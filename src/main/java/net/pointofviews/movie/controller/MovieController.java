package net.pointofviews.movie.controller;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.dto.SearchMovieCriteria;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.request.CreateMovieContentRequest;
import net.pointofviews.movie.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController implements MovieSpecification {

    @Override
    @PostMapping
    public ResponseEntity<?> createMovie(@RequestBody CreateMovieRequest request) {
        return null;
    }

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

    @Override
    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long movieId) {
        return null;
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchTMDBMovieListResponse>> tmdbSearchMovieList(@RequestParam String title) {
        return null;
    }

    @Override
    @PostMapping("/{movieId}/images")
    public ResponseEntity<?> createImage(Long movieId, CreateMovieContentRequest createMovieContentRequest) {
        return null;
    }

    @Override
    @PostMapping("/{movieId}/videos")
    public ResponseEntity<?> createVideo(Long movieId, CreateMovieContentRequest createMovieContentRequest) {
        return null;
    }

    @Override
    @DeleteMapping("/{movieId}/images/{id}")
    public ResponseEntity<?> deleteImage(Long movieId, Long id) {
        return null;
    }

    @Override
    @DeleteMapping("/{movieId}/videos/{id}")
    public ResponseEntity<?> deleteVideo(Long movieId, Long id) {
        return null;
    }

    @Override
    @PutMapping("/{movieId}/likes")
    public ResponseEntity<?> createMovieLike(Long movieId) {
        return null;
    }

}