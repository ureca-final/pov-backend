package net.pointofviews.movie.dto.response;

import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieGenre;

import java.util.List;

public record BatchDiscoverMovieResponse(
        Movie movie,
        List<MovieGenre> genres
) {
}
