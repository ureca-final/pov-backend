package net.pointofviews.movie.batch.credit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.movie.dto.response.CreditProcessorResponse;
import net.pointofviews.people.domain.People;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbMovieCreditWriter implements ItemWriter<CreditProcessorResponse> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void write(Chunk<? extends CreditProcessorResponse> items) {
        for (CreditProcessorResponse item : items) {
            List<People> crewPeoples = item.crewPeoples().stream()
                    .map(this::findOrSavePeople)
                    .toList();
            List<People> castPeoples = item.castPeoples().stream()
                    .map(this::findOrSavePeople)
                    .toList();

            boolean hasCrewOrCast = false;

            for (int i = 0; i < crewPeoples.size(); i++) {
                MovieCrew crew = item.crews().get(i);
                crew.updatePeople(crewPeoples.get(i));
                crew.updateMovie(item.movie());
                hasCrewOrCast |= findOrSaveMovieCrew(crew);
            }

            for (int i = 0; i < castPeoples.size(); i++) {
                MovieCast cast = item.casts().get(i);
                cast.updatePeople(castPeoples.get(i));
                cast.updateMovie(item.movie());
                hasCrewOrCast |= findOrSaveMovieCast(cast);
            }

            if (!hasCrewOrCast) {
                log.info("Deleting movie: {}", item.movie().getId());
                entityManager.remove(entityManager.contains(item.movie()) ? item.movie() : entityManager.merge(item.movie()));
            } else {
                entityManager.merge(item.movie());
            }
        }
    }

    private People findOrSavePeople(People person) {
        People existingPerson = findByTmdbId(person.getTmdbId());
        if (existingPerson != null) {
            return existingPerson;
        }
        entityManager.persist(person);
        return person;
    }

    private People findByTmdbId(Integer tmdbId) {
        return entityManager.createQuery("SELECT p FROM People p WHERE p.tmdbId = :tmdbId", People.class)
                .setParameter("tmdbId", tmdbId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    private boolean findOrSaveMovieCrew(MovieCrew crew) {
        boolean exists = existsMovieCrew(crew.getMovie(), crew.getPeople(), crew.getRole());
        if (!exists) {
            entityManager.persist(crew);
            return true;
        }
        return false;
    }

    private boolean existsMovieCrew(Movie movie, People people, String role) {
        return entityManager.createQuery(
                        "SELECT COUNT(mc) > 0 FROM MovieCrew mc WHERE mc.movie = :movie AND mc.people = :people AND mc.role = :role", Boolean.class)
                .setParameter("movie", movie)
                .setParameter("people", people)
                .setParameter("role", role)
                .getSingleResult();
    }

    private boolean findOrSaveMovieCast(MovieCast cast) {
        boolean exists = existsMovieCast(cast.getMovie(), cast.getPeople(), cast.getRoleName());
        if (!exists) {
            entityManager.persist(cast);
            return true;
        }
        return false;
    }

    private boolean existsMovieCast(Movie movie, People people, String roleName) {
        return entityManager.createQuery(
                        "SELECT COUNT(mc) > 0 FROM MovieCast mc WHERE mc.movie = :movie AND mc.people = :people AND mc.roleName = :roleName", Boolean.class)
                .setParameter("movie", movie)
                .setParameter("people", people)
                .setParameter("roleName", roleName)
                .getSingleResult();
    }
}
