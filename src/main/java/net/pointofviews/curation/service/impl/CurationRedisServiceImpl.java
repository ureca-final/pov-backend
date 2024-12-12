package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.dto.request.SaveTodayCurationRequest;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.exception.CurationMovieException;
import net.pointofviews.curation.service.CurationRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.cache.interceptor.SimpleKeyGenerator.generateKey;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationRedisServiceImpl implements CurationRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 캐시에 영화 목록 저장
     */
    @Override
    @Transactional
    public Set<Long> saveMoviesToCuration(Long curationId, Set<Long> movieIds) {
        String key = generateMovieKey(curationId);

        // 이미 데이터가 존재하면 저장하지 않고 예외 발생
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw CurationException.CurationAlreadyExists(curationId);
        }

        redisTemplate.opsForSet().add(key, movieIds.stream().map(String::valueOf).toArray());
        return movieIds;
    }


    /**
     * 캐시에서 영화 목록 조회
     */
    @Override
    public Set<Long> readMoviesForCuration(Long curationId) {
        String key = generateMovieKey(curationId);

        // 키가 존재하지 않으면 빈 SET 반환
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            return Collections.emptySet();

        }

        Set<Object> cachedMovies = redisTemplate.opsForSet().members(key);
        Set<Long> movieIds = new HashSet<>();

        if (cachedMovies != null) {
            cachedMovies.forEach(movie -> {
                try {
                    movieIds.add(Long.valueOf(movie.toString())); // 모든 값을 Long으로 변환
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Redis 데이터 변환 실패: " + movie, e);
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
        String key = generateMovieKey(curationId);

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
        String key = generateMovieKey(curationId);

        // 키가 존재하지 않으면 예외 발생
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw CurationMovieException.CurationMovieKeyNotFound();
        }

        redisTemplate.delete(key);
    }

    @Override
    @Transactional
    public void saveTodayCurationId(Long curationId) {
        String key = generateDateKey(LocalDate.now());
        redisTemplate.opsForSet().add(key, String.valueOf(curationId)); // Long -> String 변환

    }

    @Override
    public Set<Long> readTodayCurationId() {
        String key = generateDateKey(LocalDate.now());
        // Redis에서 데이터 읽기
        Set<Object> cachedIds = redisTemplate.opsForSet().members(key);

        if (cachedIds == null) {
            return Collections.emptySet();
        }

        // String 또는 Integer -> Long 변환
        return cachedIds.stream()
                .map(id -> {
                    try {
                        return Long.valueOf(id.toString());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Redis 데이터 변환 실패: " + id, e);
                    }
                })
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void saveTodayCurationDetail(Long curationId, SaveTodayCurationRequest saveTodayCurationRequest) {
        String key = generateCurationKey(curationId);

        // Redis에 큐레이션 데이터를 저장
        Map<String, Object> curationData = new HashMap<>();
        curationData.put("title", saveTodayCurationRequest.curationTitle());

        // movies 데이터를 String으로 변환하여 저장
        List<Map<String, Object>> movieList = saveTodayCurationRequest.curationMovies().stream()
                .map(movie -> {
                    Map<String, Object> movieMap = new HashMap<>();
                    movieMap.put("title", movie.title());
                    movieMap.put("poster", movie.poster());
                    movieMap.put("released", movie.released() != null ? movie.released().toString() : null); // LocalDate -> String
                    movieMap.put("movieLikeCount", movie.movieLikeCount().toString()); // Long -> String
                    movieMap.put("movieReviewCount", movie.movieReviewCount().toString()); // Long -> String
                    return movieMap;
                })
                .collect(Collectors.toList());

        curationData.put("movies", movieList);

        redisTemplate.opsForHash().putAll(key, curationData);
    }

    @Override
    public ReadUserCurationResponse readTodayCurationDetail(Long curationId) {
        String key = generateCurationKey(curationId);

        // Redis에서 데이터를 조회
        Map<Object, Object> curationData = redisTemplate.opsForHash().entries(key);

        if (curationData == null || curationData.isEmpty()) {
            return new ReadUserCurationResponse("", List.of());
        }

        String title = (String) curationData.get("title");
        List<ReadUserCurationMovieResponse> movies = new ArrayList<>();

        if (curationData.get("movies") != null) {
            List<?> rawMovies = (List<?>) curationData.get("movies");
            rawMovies.forEach(movie -> {
                if (movie instanceof Map) {
                    Map<String, Object> movieMap = (Map<String, Object>) movie;
                    movies.add(new ReadUserCurationMovieResponse(
                            (String) movieMap.get("title"),
                            (String) movieMap.get("poster"),
                            movieMap.get("released") != null ? LocalDate.parse((String) movieMap.get("released")) : null,
                            Long.valueOf((String) movieMap.get("movieLikeCount")),
                            Long.valueOf((String) movieMap.get("movieReviewCount"))
                    ));
                }
            });
        }

        return new ReadUserCurationResponse(title, movies);
    }

    private String generateMovieKey(Long curationId) {
        return "curation:movies:" + curationId;
    }

    private String generateDateKey(LocalDate date) {
        return "curation:" + date.toString();
    }

    private String generateCurationKey(Long curationId) {
        return "curation:" + curationId;
    }
}
