package net.pointofviews.movie.batch.country;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import net.pointofviews.country.domain.Country;
import net.pointofviews.movie.batch.utils.ApiRateLimiter;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCountry;
import net.pointofviews.movie.dto.response.SearchFilteredMovieDetailResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TMDbMovieCountryProcessor implements ItemProcessor<Movie, List<MovieCountry>> {

    private final MovieTMDbSearchService searchService;
    private final ApiRateLimiter batchApiRateLimiter;

    @PersistenceContext
    private EntityManager entityManager;

    public List<MovieCountry> process(Movie item) {
        entityManager.detach(item);
        batchApiRateLimiter.limit();
        SearchFilteredMovieDetailResponse detailsResponse = searchService.searchDetailsMovie(item.getTmdbId().toString());
        List<String> stringCountries = detailsResponse.originCountries();

        List<MovieCountry> movieCountries = stringCountries.stream()
                .map(countryName -> new MovieCountry(new Country(new Locale("", countryName).getDisplayCountry(Locale.KOREAN))))
                .toList();

        movieCountries.forEach(movieCountry -> movieCountry.updateMovie(item));
        return movieCountries;
    }
}
