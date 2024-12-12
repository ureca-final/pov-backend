package net.pointofviews.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GENRE_PREFERENCES_KEY = "genre:preferences:";

    public void saveGenresToRedis(UUID memberId, List<String> genreCodes) {
        genreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            redisTemplate.opsForSet().add(key, memberId.toString());
        });
    }

    public void updateGenresInRedis(UUID memberId, List<String> existingGenreCodes, List<String> newGenreCodes) {
        // 기존 장르 삭제
        existingGenreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            redisTemplate.opsForSet().remove(key, memberId.toString());
        });

        // 새로운 장르 추가
        newGenreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            redisTemplate.opsForSet().add(key, memberId.toString());
        });
    }
}
