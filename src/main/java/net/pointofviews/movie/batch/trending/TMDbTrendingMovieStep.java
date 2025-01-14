package net.pointofviews.movie.batch.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.core.Step;
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
                .listener(trendingMovieWriter)
                .build();
    }
}
