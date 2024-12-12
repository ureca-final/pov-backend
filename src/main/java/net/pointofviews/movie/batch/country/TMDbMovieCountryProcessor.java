package net.pointofviews.movie.batch.country;

import lombok.RequiredArgsConstructor;
import net.pointofviews.country.domain.Country;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCountry;
import net.pointofviews.movie.dto.response.SearchFilteredMovieDetailResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TMDbMovieCountryProcessor implements ItemProcessor<Movie, Movie> {
    private final MovieTMDbSearchService searchService;

    public Movie process(Movie item) {
        SearchFilteredMovieDetailResponse detailsResponse = searchService.searchDetailsMovie(item.getTmdbId().toString());
        List<String> stringCountries = detailsResponse.originCountries();

        List<MovieCountry> movieCountries = stringCountries.stream()
                .map(countryName -> new MovieCountry(new Country(countryName)))
                .toList();

        movieCountries.forEach(item::addCountry);
        return item;
    }
}
