package net.pointofviews.movie.batch.country;

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
public class TMDbMovieCountryStep {

    private final JobRepository jobRepository;
    private final JpaPagingItemReader<Movie> movieCountryJpaReader;
    private final TMDbMovieCountryWriter writer;
    private final TMDbMovieCountryProcessor processor;

    @Bean
    public Step tmdbMovieCountryStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("tmdbMovieCountryStep", jobRepository)
                .<Movie, Movie>chunk(20, transactionManager)
                .reader(movieCountryJpaReader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
