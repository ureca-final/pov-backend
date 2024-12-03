package net.pointofviews.curation.service;

import java.util.Set;

public interface CurationMovieCacheService {
    Set<Long> readMoviesForCuration(Long curationId);
    Set<Long> saveMoviesToCuration(Long curationId, Set<Long> movieIds);
    Set<Long> deleteMovieFromCuration(Long curationId, Long movieId);
    void deleteAllMoviesForCuration(Long curationId);
}
