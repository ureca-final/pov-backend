package net.pointofviews.movie.batch.country;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.batch.listener.MovieChunkListener;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCountry;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TMDbMovieCountryStep {

    private final JobRepository jobRepository;
    private final JpaPagingItemReader<Movie> movieCountryJpaReader;
    private final TMDbMovieCountryWriter writer;
    private final TMDbMovieCountryProcessor processor;

    @Bean
    public Step tmdbMovieCountryStep(PlatformTransactionManager transactionManager,
                                     MovieChunkListener movieChunkListener) {
        return new StepBuilder("tmdbMovieCountryStep", jobRepository)
                .<Movie, List<MovieCountry>>chunk(100, transactionManager)
                .reader(movieCountryJpaReader)
                .processor(processor)
                .writer(writer)
                .listener(movieChunkListener)
                .build();
    }
}
