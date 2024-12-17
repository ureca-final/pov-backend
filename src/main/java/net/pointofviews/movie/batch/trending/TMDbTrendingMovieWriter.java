package net.pointofviews.movie.batch.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbTrendingMovieWriter implements ItemWriter<Movie> {
    private static final String TMDB_TRENDING_KEY = "trending:";
    private static final int MAX_SIZE = 20;
    private int count = 0;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void write(Chunk<? extends Movie> chunk) throws Exception {
        List<? extends Movie> items = chunk.getItems();

        for (Movie movie : items) {
            if (movie != null) {
                long currentCount = countTrendingMovies() + count;

                if (currentCount < MAX_SIZE) {
                    redisTemplate.opsForValue().set(TMDB_TRENDING_KEY + movie.getTmdbId(), movie.toString());
                    log.info("[write] 저장된 트렌딩 영화: {}, 현재 카운트: {}", movie.getTmdbId(), currentCount + 1);
                    count++;
                } else {
                    log.info("[write] 저장 중지 - 이미 {}개 영화가 저장되었습니다.", currentCount);
                    break;
                }
            }
        }
    }

    public long countTrendingMovies() {
        Set<String> keys = redisTemplate.keys("trending:*");
        return keys.size();
    }
}