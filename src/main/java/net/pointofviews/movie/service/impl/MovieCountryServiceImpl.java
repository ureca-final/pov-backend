package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.country.domain.Country;
import net.pointofviews.country.repository.CountryRepository;
import net.pointofviews.movie.service.MovieCountryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MovieCountryServiceImpl implements MovieCountryService {

    private final CountryRepository countryRepository;

    public Country saveMovieCountries(Country country) {
        return countryRepository.findByName(country.getName()).orElseGet(
                () -> countryRepository.save(country)
        );
    }
}
