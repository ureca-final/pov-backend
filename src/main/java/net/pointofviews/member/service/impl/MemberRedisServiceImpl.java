package net.pointofviews.member.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.member.service.MemberRedisService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberRedisServiceImpl implements MemberRedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private static final String GENRE_PREFERENCES_KEY = "genre:preferences:";

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveGenresToRedis(UUID memberId, List<String> genreCodes) {
        genreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            stringRedisTemplate.opsForSet().add(key, memberId.toString());
        });
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateGenresInRedis(UUID memberId, List<String> existingGenreCodes, List<String> newGenreCodes) {
        // 기존 장르 삭제
        existingGenreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            stringRedisTemplate.opsForSet().remove(key, memberId.toString());
        });

        // 새로운 장르 추가
        newGenreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            stringRedisTemplate.opsForSet().add(key, memberId.toString());
        });
    }
}
