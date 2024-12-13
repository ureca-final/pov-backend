package net.pointofviews.country.service;

import net.pointofviews.country.domain.Country;

import java.util.Optional;

public interface CountryService {
    Country saveCountry(String name);

    Optional<Country> findCountryByName(String name);
}
