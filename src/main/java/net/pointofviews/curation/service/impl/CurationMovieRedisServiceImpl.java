package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.exception.CurationMovieException;
import net.pointofviews.curation.service.CurationMovieRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationMovieRedisServiceImpl implements CurationMovieRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String generateKey(Long curationId) {
        return "curation:movies:" + curationId;
    }

    /**
     * 캐시에 영화 목록 저장
     */
    @Override
    @Transactional
    public Set<Long> saveMoviesToCuration(Long curationId, Set<Long> movieIds) {
        String key = generateKey(curationId);

        // 이미 데이터가 존재하면 저장하지 않고 예외 발생
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw CurationException.CurationAlreadyExists(curationId);
        }

        redisTemplate.opsForSet().add(key, movieIds.stream().map(Long::valueOf).toArray());
        return movieIds;
    }


    /**
     * 캐시에서 영화 목록 조회
     */
    @Override
    public Set<Long> readMoviesForCuration(Long curationId) {
        String key = generateKey(curationId);

        // 키가 존재하지 않으면 예외 발생
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw CurationMovieException.CurationMovieKeyNotFound();
        }

        Set<Object> cachedMovies = redisTemplate.opsForSet().members(key);
        Set<Long> movieIds = new HashSet<>();

        if (cachedMovies != null) {
            cachedMovies.forEach(movie -> {
                if (movie instanceof Integer) {
                    movieIds.add(((Integer) movie).longValue()); // Integer -> Long 변환
                } else if (movie instanceof Long) {
                    movieIds.add((Long) movie);
                }
            });
        }

        return movieIds;
    }

    /**
     * 캐시에 영화 목록 수정 (삭제 후 등록)
     */
    @Override
    @Transactional
    public Set<Long> updateMoviesToCuration(Long curationId, Set<Long> movieIds) {
        String key = generateKey(curationId);

        // 키가 존재하지 않으면 예외 발생
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw CurationMovieException.CurationMovieKeyNotFound();
        }

        Boolean isDeleted = redisTemplate.delete(key);
        redisTemplate.opsForSet().add(key, movieIds.stream().map(Long::valueOf).toArray());

        return movieIds;
    }

    /**
     * 캐시에서 모든 영화 삭제
     */
    @Override
    @Transactional
    public void deleteAllMoviesForCuration(Long curationId) {
        String key = generateKey(curationId);

        // 키가 존재하지 않으면 예외 발생
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw CurationMovieException.CurationMovieKeyNotFound();
        }

        redisTemplate.delete(key);
    }
}
