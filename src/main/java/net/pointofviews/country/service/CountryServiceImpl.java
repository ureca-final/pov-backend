package net.pointofviews.country.service;

import lombok.RequiredArgsConstructor;
import net.pointofviews.country.domain.Country;
import net.pointofviews.country.repository.CountryRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static net.pointofviews.country.exception.CountryException.notFound;

@Service
@Transactional
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    @CachePut(value = "country", key = "#result.name", cacheManager = "cacheManagerWithTTL")
    public Country saveCountry(String name) {
        try {
            Country country = new Country(name);
            return countryRepository.save(country);
        } catch (DataIntegrityViolationException e) {
            return countryRepository.findByName(name)
                    .orElseThrow(() -> notFound("Country not found after duplicate exception"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Country> findCountryByName(String name) {
        return countryRepository.findByName(name);
    }
}