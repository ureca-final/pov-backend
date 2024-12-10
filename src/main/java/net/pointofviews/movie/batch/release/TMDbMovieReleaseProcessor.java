package net.pointofviews.movie.batch.release;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.KoreanFilmRating;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.dto.response.SearchReleaseApiResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbMovieReleaseProcessor implements ItemProcessor<Movie, Movie> {

    private final MovieTMDbSearchService searchService;

    @Override
    public Movie process(Movie item) {
        SearchReleaseApiResponse searchReleaseApiResponse = searchService.searchReleaseDate(item.getTmdbId().toString());
        SearchReleaseApiResponse.Result.ReleaseDate bestResult = searchReleaseApiResponse.results().get(0).release_dates().get(0);

        String releaseDate = bestResult.release_date();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(releaseDate);
        LocalDate localDate = zonedDateTime.toLocalDate();

        String certification = bestResult.certification();
        KoreanFilmRating rating = KoreanFilmRating.of(certification);

        item.updateMovie(localDate, rating);
        return item;
    }
}
