package net.pointofviews.movie.batch.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbTrendingMovieProcessor implements ItemProcessor<Integer, Movie> {

    private final MovieRepository movieRepository;

    @Override
    public Movie process(final Integer tmdbId) throws Exception {

        return movieRepository.findMovieByTmdbId(tmdbId)
                .orElse(null);
    }
}
