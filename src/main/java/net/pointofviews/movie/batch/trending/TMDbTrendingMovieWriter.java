package net.pointofviews.movie.batch.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.exception.RedisException;
import net.pointofviews.common.service.RedisService;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbTrendingMovieWriter implements ItemWriter<Movie>, StepExecutionListener {
    private static final String TMDB_TRENDING_KEY = "trending";
    private static final int MAX_SIZE = 20;

    private final RedisService redisService;

    @Override
    public void write(Chunk<? extends Movie> chunk) throws Exception {
        List<? extends Movie> items = chunk.getItems();

        for (Movie movie : items) {
            if (movie != null) {
                long currentCount = countTrendingMovies();

                if (currentCount < MAX_SIZE) {
                    redisService.addToSet(TMDB_TRENDING_KEY, movie.getId().toString());
                    log.info("[write] 저장된 트렌딩 영화: {}, 현재 카운트: {}", movie.getTmdbId(), currentCount + 1);
                } else {
                    log.info("[write] 저장 중지 - 이미 {}개 영화가 저장되었습니다.", currentCount);
                    break;
                }
            }
        }
    }

    public long countTrendingMovies() {
        Set<String> keys = redisService.getSetMembers("trending");
        return keys.size();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("트렌딩 영화 삭제");
        if (redisService.getKeys(TMDB_TRENDING_KEY).isEmpty()) {
            return;
        }

        boolean isDeleted = redisService.removeKey(TMDB_TRENDING_KEY);
        if (!isDeleted) {
            throw RedisException.redisServerError(TMDB_TRENDING_KEY);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long savedMoviesCount = countTrendingMovies();

        if (savedMoviesCount >= 20) {
            log.info("트렌딩 영화 배치 완료: {} 개 저장", savedMoviesCount);
            stepExecution.setExitStatus(ExitStatus.COMPLETED);
            redisService.setTTL(TMDB_TRENDING_KEY, Duration.ofDays(1));
            return ExitStatus.COMPLETED;
        }

        log.info("현재 저장된 트렌딩 영화 수: {}", savedMoviesCount);
        return stepExecution.getExitStatus();
    }
}