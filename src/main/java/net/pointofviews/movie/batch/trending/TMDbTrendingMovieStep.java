package net.pointofviews.movie.batch.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TMDbTrendingMovieStep {

    private static final int CHUNK_SIZE = 100;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TMDbTrendingMovieReader trendingMovieReader;
    private final TMDbTrendingMovieProcessor trendingMovieProcessor;
    private final TMDbTrendingMovieWriter trendingMovieWriter;

    @Bean
    public Step trendingMovieStep() {
        return new StepBuilder("trendingMovieStep", jobRepository)
                .<Integer, Movie>chunk(CHUNK_SIZE, transactionManager)
                .reader(trendingMovieReader)
                .processor(trendingMovieProcessor)
                .writer(trendingMovieWriter)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        long savedMoviesCount = trendingMovieWriter.countTrendingMovies();

                        if (savedMoviesCount >= 20) {
                            log.info("트렌딩 영화 배치 완료: {} 개 저장", savedMoviesCount);
                            stepExecution.setExitStatus(ExitStatus.COMPLETED);
                            return ExitStatus.COMPLETED;
                        }

                        log.info("현재 저장된 트렌딩 영화 수: {}", savedMoviesCount);
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }
}
