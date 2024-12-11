package net.pointofviews.movie.batch.country;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.pointofviews.country.domain.Country;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCountry;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TMDbMovieCountryWriter implements ItemWriter<Movie> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends Movie> movies) {
        Map<String, Country> countryCache = new HashMap<>();

        for (Movie movie : movies) {
            for (MovieCountry movieCountry : movie.getCountries()) {
                Country transientCountry = movieCountry.getCountry();

                Country persistedCountry = countryCache.computeIfAbsent(
                        transientCountry.getName(),
                        this::findOrSaveCountry
                );

                movieCountry.updateCountry(persistedCountry);
            }

            entityManager.merge(movie);
        }
        entityManager.flush();
    }


    private Country findOrSaveCountry(String name) {
        Country country = findCountryByName(name);
        if (country == null) {
            country = new Country(name);
            entityManager.persist(country);
            entityManager.flush();
        }
        return country;
    }

    private Country findCountryByName(String name) {
        return entityManager.createQuery("SELECT c FROM Country c WHERE c.name = :name", Country.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}