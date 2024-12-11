package net.pointofviews.movie.batch.credit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.movie.dto.response.CreditProcessorResponse;
import net.pointofviews.people.domain.People;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

            entityManager.flush();

            for (int i = 0; i < crewPeoples.size(); i++) {
                MovieCrew crew = item.crews().get(i);
                crew.updatePeople(crewPeoples.get(i));
                crew.updateMovie(item.movie());
                entityManager.merge(crew);
            }

            for (int i = 0; i < castPeoples.size(); i++) {
                MovieCast cast = item.casts().get(i);
                cast.updatePeople(castPeoples.get(i));
                cast.updateMovie(item.movie());
                entityManager.merge(cast);
            }

            entityManager.merge(item.movie());
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
}


