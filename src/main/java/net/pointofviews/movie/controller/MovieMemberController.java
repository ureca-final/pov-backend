package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.service.MovieContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieMemberController implements MovieMemberSpecification {

    private final MovieContentService movieContentService;

    @Override
    @PutMapping("/{movieId}/likes")
    public ResponseEntity<?> createMovieLike(@PathVariable Long movieId) {
        return null;
    }
}
