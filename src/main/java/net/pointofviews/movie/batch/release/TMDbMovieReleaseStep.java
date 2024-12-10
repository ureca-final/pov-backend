package net.pointofviews.movie.batch.release;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TMDbMovieReleaseStep {

    private final JobRepository jobRepository;
    private final JpaPagingItemReader<Movie> reader;
    private final TMDbMovieReleaseProcessor processor;
    private final TMDbMovieReleaseWriter writer;

    @Bean
    public Step tmdbMovieReleaseStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("tmdbMovieReleaseStep", jobRepository)
                .<Movie, Movie>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
