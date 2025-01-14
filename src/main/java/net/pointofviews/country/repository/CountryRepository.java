package net.pointofviews.country.repository;

import net.pointofviews.country.domain.Country;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    @Cacheable(cacheNames = "country", cacheManager = "cacheManagerWithTTL", key = "#name")
    Optional<Country> findByName(String name);
}