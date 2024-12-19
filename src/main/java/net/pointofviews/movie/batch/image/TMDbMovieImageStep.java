package net.pointofviews.movie.batch.image;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.batch.dto.MovieContentsDto;
import net.pointofviews.movie.batch.listener.MovieChunkListener;
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
public class TMDbMovieImageStep {

    private final JobRepository jobRepository;
    private final JpaPagingItemReader<Movie> movieImageJpaReader;
    private final TMDbMovieImageProcessor processor;
    private final TMDbMovieImageWriter writer;

    @Bean
    public Step tmdbMovieImageStep(PlatformTransactionManager transactionManager,
                                   MovieChunkListener movieChunkListener) {
        return new StepBuilder("tmdbMovieImageStep", jobRepository)
                .<Movie, MovieContentsDto>chunk(100, transactionManager)
                .reader(movieImageJpaReader)
                .processor(processor)
                .writer(writer)
                .listener(movieChunkListener)
                .build();
    }
}