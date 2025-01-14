package net.pointofviews.people.repository;

import net.pointofviews.people.domain.People;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<People, Long> {
    Optional<People> findByTmdbId(Integer tmdbId);
}
