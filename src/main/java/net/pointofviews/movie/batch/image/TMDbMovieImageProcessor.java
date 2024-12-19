package net.pointofviews.movie.batch.image;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.batch.dto.MovieContentsDto;
import net.pointofviews.movie.batch.utils.ApiRateLimiter;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieContent;
import net.pointofviews.movie.domain.MovieContentType;
import net.pointofviews.movie.dto.response.SearchMovieImageApiResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TMDbMovieImageProcessor implements ItemProcessor<Movie, MovieContentsDto> {

    private final MovieTMDbSearchService searchService;
    private final ApiRateLimiter batchApiRateLimiter;
    private static final String MOVIE_URL = "https://image.tmdb.org/t/p/w342";

    @Override
    public MovieContentsDto process(Movie movie) {
        batchApiRateLimiter.limit();
        SearchMovieImageApiResponse response = searchService.searchImageMovie(movie.getTmdbId().toString());

        List<MovieContent> posters = response.posters().stream()
                .sorted(Comparator.comparingDouble(SearchMovieImageApiResponse.Poster::vote_average).reversed())
                .map(p -> MovieContent.builder()
                        .contentType(MovieContentType.IMAGE)
                        .content(MOVIE_URL + p.file_path())
                        .movie(movie)
                        .build()).limit(5).toList();

        return new MovieContentsDto(posters, movie.getId());
    }
}
