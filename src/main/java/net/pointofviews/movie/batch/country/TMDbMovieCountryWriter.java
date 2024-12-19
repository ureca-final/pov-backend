package net.pointofviews.movie.batch.country;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import net.pointofviews.country.domain.Country;
import net.pointofviews.country.service.CountryService;
import net.pointofviews.movie.domain.MovieCountry;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TMDbMovieCountryWriter implements ItemWriter<List<MovieCountry>> {

    @PersistenceContext
    private EntityManager entityManager;

    private final CountryService countryService;

    @Override
    public void write(Chunk<? extends List<MovieCountry>> movieCountryChunk) {
        List<MovieCountry> movieCountryList = new ArrayList<>();

        for (List<MovieCountry> movieCountries : movieCountryChunk) {
            for (MovieCountry movieCountry : movieCountries) {
                String countryName = movieCountry.getCountry().getName();

                Country persistedCountry = findOrSaveCountry(countryName);
                movieCountry.updateCountry(persistedCountry);
                movieCountryList.add(movieCountry);
            }
        }

        movieCountryList.forEach(entityManager::persist);
        entityManager.flush();
        entityManager.clear();
    }

    private Country findOrSaveCountry(String name) {
        return countryService.findCountryByName(name).orElseGet(() -> countryService.saveCountry(name));
    }
}