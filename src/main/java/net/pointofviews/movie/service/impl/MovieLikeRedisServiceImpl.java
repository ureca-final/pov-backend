package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.MovieLikeCountDto;
import net.pointofviews.movie.dto.MovieLikeCountListDto;
import net.pointofviews.movie.dto.MovieLikeDto;
import net.pointofviews.movie.dto.MovieLikeListDto;
import net.pointofviews.movie.exception.MovieLikeException;
import net.pointofviews.movie.service.MovieLikeRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieLikeRedisServiceImpl implements MovieLikeRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 좋아요
    @Override
    @Transactional
    public void saveLikedToRedis(Long movieId, Member member) {
        UUID memberId = member.getId();
        String key = generateLikeKey(movieId, memberId);
        String countKey = generateLikeCountKey(movieId);

        // 이미 좋아요 누른 영화 중복 예외 처리
        if(readIsLikedFromRedis(movieId, member)){
            throw MovieLikeException.alreadyLiked(movieId, memberId);
        }

        redisTemplate.opsForValue().set(key, true); // 좋아요 상태 저장
        redisTemplate.opsForValue().increment(countKey, 1L); // 좋아요 개수 증가
    }

    // 좋아요 취소
    @Override
    @Transactional
    public void saveDisLikedToRedis(Long movieId, Member member)  {
        UUID memberId = member.getId();
        String key = generateLikeKey(movieId, memberId);
        String countKey = generateLikeCountKey(movieId);

        // 이미 좋아요 취소 한 영화 중복 예외 처리
        if(!readIsLikedFromRedis(movieId, member)){
            throw MovieLikeException.alreadyDisliked(movieId, memberId);
        }

        redisTemplate.opsForValue().set(key, false); // 좋아요 상태 저장
        redisTemplate.opsForValue().decrement(countKey, 1L); // 좋아요 개수 감소

    }

    @Override
    public boolean readIsLikedFromRedis(Long movieId, Member member) {
        UUID memberId = member.getId();
        String key = generateLikeKey(movieId, memberId);
        Boolean isLiked = (Boolean) redisTemplate.opsForValue().get(key);
        return Boolean.TRUE.equals(isLiked); // null 또는 false인 경우 false 반환
    }

    @Override
    public Long readLikedCountFromRedis(Long movieId) {
        String key = generateLikeCountKey(movieId);
        Object value = redisTemplate.opsForValue().get(key);

        // 값이 Integer로 저장되었을 경우 Long으로 변환
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }

        // 값이 없을 경우 기본값 0 반환
        return 0L;
    }

    @Override
    public MovieLikeListDto readAllLikedDataFromRedis() {
        Set<String> keys = redisTemplate.keys("Liked:*");
        List<MovieLikeDto> likes = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                Boolean isLiked = (Boolean) redisTemplate.opsForValue().get(key);
                if (Boolean.TRUE.equals(isLiked)) {
                    String[] parts = key.split(":");
                    Long movieId = Long.parseLong(parts[1]);
                    UUID memberId = UUID.fromString(parts[2]);

                    likes.add(new MovieLikeDto(movieId, memberId, isLiked));
                }
            }
        }
        return new MovieLikeListDto(likes);
    }

    @Override
    public MovieLikeCountListDto readAllLikedCountFromRedis() {
        Set<String> keys = redisTemplate.keys("LikedCount:*");
        List<MovieLikeCountDto> counts = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                Object value = redisTemplate.opsForValue().get(key);

                Long count = value != null ? Long.valueOf(value.toString()) : 0L;
                Long movieId = Long.parseLong(key.split(":")[1]);


                counts.add(new MovieLikeCountDto(movieId, count));
            }
        }
        return new MovieLikeCountListDto(counts);
    }


    private String generateLikeKey(Long movieId, UUID memberId)  {
        return "Liked:" + movieId+ ":" + memberId;
    }

    private String generateLikeCountKey(Long movieId)  {
        return "LikedCount:" + movieId;
    }
}
