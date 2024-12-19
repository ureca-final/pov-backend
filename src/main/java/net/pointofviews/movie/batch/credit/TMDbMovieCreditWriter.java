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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbMovieCreditWriter implements ItemWriter<CreditProcessorResponse> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void write(Chunk<? extends CreditProcessorResponse> items) {
        // 캐시를 위한 Map 사용
        Map<Integer, People> peopleCache = loadPeopleCache(items);
        Map<Integer, MovieCrew> crewCache = new HashMap<>();
        Map<Integer, MovieCast> castCache = new HashMap<>();

        for (CreditProcessorResponse item : items) {
            Movie movie = item.movie();

            // People 저장
            List<People> crewPeoples = item.crewPeoples().stream()
                    .map(p -> findOrCreatePeople(p, peopleCache))
                    .toList();

            List<People> castPeoples = item.castPeoples().stream()
                    .map(p -> findOrCreatePeople(p, peopleCache))
                    .toList();

            // MovieCrew 저장
            for (int i = 0; i < crewPeoples.size(); i++) {
                MovieCrew crew = item.crews().get(i);
                crew.updatePeople(crewPeoples.get(i));
                crew.updateMovie(movie);
                batchSaveMovieCrew(crew, crewCache);
            }

            // MovieCast 저장
            for (int i = 0; i < castPeoples.size(); i++) {
                MovieCast cast = item.casts().get(i);
                cast.updatePeople(castPeoples.get(i));
                cast.updateMovie(movie);
                batchSaveMovieCast(cast, castCache);
            }

            // Movie 상태 업데이트
            entityManager.merge(movie);

        }

        flushAndClear();
    }

    // People 캐시 로드
    private Map<Integer, People> loadPeopleCache(Chunk<? extends CreditProcessorResponse> items) {
        List<Integer> tmdbIds = items.getItems().stream()
                .flatMap(item -> Stream.concat(item.crewPeoples().stream(), item.castPeoples().stream()))
                .map(People::getTmdbId)
                .distinct()
                .toList();

        return entityManager.createQuery(
                        "SELECT p FROM People p WHERE p.tmdbId IN :tmdbIds", People.class)
                .setParameter("tmdbIds", tmdbIds)
                .getResultStream()
                .collect(Collectors.toMap(People::getTmdbId, p -> p));
    }

    // People 저장 또는 캐시 사용
    private People findOrCreatePeople(People person, Map<Integer, People> peopleCache) {
        return peopleCache.computeIfAbsent(person.getTmdbId(), tmdbId -> {
            entityManager.persist(person);
            return person;
        });
    }

    // MovieCrew Batch 저장
    private boolean batchSaveMovieCrew(MovieCrew crew, Map<Integer, MovieCrew> crewCache) {
        if (!crewCache.containsKey(crew.hashCode())) {
            entityManager.persist(crew);
            crewCache.put(crew.hashCode(), crew);
            return true;
        }
        return false;
    }

    // MovieCast Batch 저장
    private boolean batchSaveMovieCast(MovieCast cast, Map<Integer, MovieCast> castCache) {
        if (!castCache.containsKey(cast.hashCode())) {
            entityManager.persist(cast);
            castCache.put(cast.hashCode(), cast);
            return true;
        }
        return false;
    }

    // EntityManager Batch Insert 처리
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

}
