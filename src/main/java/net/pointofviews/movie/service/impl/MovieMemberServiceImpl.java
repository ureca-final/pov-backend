package net.pointofviews.movie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.lock.DistributeLock;
import net.pointofviews.common.service.RedisService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.exception.MovieLikeException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieMemberService;
import net.pointofviews.review.exception.ReviewException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static net.pointofviews.movie.exception.MovieException.movieNotFound;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieMemberServiceImpl implements MovieMemberService {

    private final MovieRepository movieRepository;
    private final RedisService redisService;

    @DistributeLock(key = "'MovieLiked:' + #movieId + ':' + #loginMember.id")
    @Override
    public void updateMovieLike(long movieId, Member loginMember) {
        // 영화 존재 확인
        if(!movieRepository.existsById(movieId)) {
            throw movieNotFound(movieId);
        }

        String likeKey = generateLikeKey(movieId, loginMember.getId());
        String countKey = generateCountKey(movieId);


        if (isLiked(likeKey)) {
            throw MovieLikeException.alreadyLikedMovie(movieId);
        }

        redisService.setValue(likeKey, "true", Duration.ofDays(7));
        updateLikeCount(countKey, true);
    }


    @DistributeLock(key = "'MovieLiked:' + #movieId + ':' + #loginMember.id")
    @Override
    public void updateMovieDisLike(Long movieId, Member loginMember) {
        if(!movieRepository.existsById(movieId)) {
            throw movieNotFound(movieId);
        }
        String likeKey = generateLikeKey(movieId, loginMember.getId());
        String countKey = generateCountKey(movieId);


        if (!isLiked(likeKey)) {
            throw MovieLikeException.alreadyDislikedMovie(movieId);
        }

        redisService.setValue(likeKey, "false", Duration.ofDays(7));
        updateLikeCount(countKey, false);
    }

    // 키 생성 메서드
    private String generateLikeKey(Long movieId, UUID memberId)  {
        return "MovieLiked:" + movieId+ ":" + memberId;
    }

    private String generateCountKey(Long movieId)  {
        return "MovieLikedCount:" + movieId;
    }

    // 좋아요 상태 조회
    private boolean isLiked(String likeKey) {
        String currentLikeStatus = redisService.getValue(likeKey);
        return "true".equals(currentLikeStatus);
    }

    // 좋아요 수 업데이트
    private void updateLikeCount(String countKey, boolean increment) {
        String currentCount = redisService.getValue(countKey);
        long count = currentCount == null ? 0L : Long.parseLong(currentCount);
        long newCount = increment ? count + 1L : count - 1L;
        redisService.setValue(countKey, String.valueOf(newCount), Duration.ofDays(7));
    }
}
