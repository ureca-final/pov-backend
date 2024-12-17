package net.pointofviews.curation.service;

import java.util.Set;

public interface CurationRedisService {
    Set<Long> saveMoviesToCuration(Long curationId, Set<Long> movieIds);
    Set<Long> readMoviesForCuration(Long curationId);
    Set<Long> updateMoviesToCuration(Long curationId, Set<Long> movieIds);
    void deleteAllMoviesForCuration(Long curationId);
    void saveTodayCurationId(Long curationId);
    Set<Long> readTodayCurationId();
}
