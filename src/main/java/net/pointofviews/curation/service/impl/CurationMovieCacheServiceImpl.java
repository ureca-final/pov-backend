package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.service.CurationMovieCacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationMovieCacheServiceImpl implements CurationMovieCacheService {

    /**
     * 캐시에서 영화 목록 조회
     */
    @Override
    @Cacheable(value = "curationMovies", key = "#curationId", cacheManager = "MovieCurationCacheManager")
    public Set<Long> readMoviesForCuration(Long curationId) {
        // 기본적으로 null 반환 (캐시 미스 시 처리 로직 필요)
        return null;
    }

    /**
     * 캐시에 영화 ID 목록 저장
     */
    @Override
    @CachePut(value = "curationMovies", key = "#curationId", cacheManager = "MovieCurationCacheManager")
    public Set<Long> saveMoviesToCuration(Long curationId, Set<Long> movieIds) {
        return movieIds;
    }

    /**
     * 캐시에 특정 영화 ID 삭제
     */
    @Override
    @CachePut(value = "curationMovies", key = "#curationId", cacheManager = "MovieCurationCacheManager")
    public Set<Long> deleteMovieFromCuration(Long curationId, Long movieId) {
        Set<Long> movieIds = readMoviesForCuration(curationId);
        if (movieIds != null) {
            movieIds.remove(movieId);
        }
        return movieIds;
    }

    /**
     * 캐시에서 큐레이션의 모든 영화 ID 삭제
     */
    @Override
    @CacheEvict(value = "curationMovies", key = "#curationId", cacheManager = "MovieCurationCacheManager")
    public void deleteAllMoviesForCuration(Long curationId) {
        // 캐시에서 키 삭제
    }
}
