package net.pointofviews.curation.service;

import net.pointofviews.curation.dto.request.SaveTodayCurationRequest;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CurationRedisService {
    Set<Long> saveMoviesToCuration(Long curationId, Set<Long> movieIds);
    Set<Long> readMoviesForCuration(Long curationId);
    Set<Long> updateMoviesToCuration(Long curationId, Set<Long> movieIds);
    void deleteAllMoviesForCuration(Long curationId);
    void saveTodayCurationId(Long curationId);
    Set<Long> readTodayCurationId();
    void saveTodayCurationDetail(Long curationId, SaveTodayCurationRequest saveTodayCurationRequest);
    ReadUserCurationResponse readTodayCurationDetail(Long curationId);

}
