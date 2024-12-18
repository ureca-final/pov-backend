package net.pointofviews.movie.batch.credit;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.batch.listener.MovieChunkListener;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.dto.response.CreditProcessorResponse;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TMDbMovieCreditStep {

    private final JobRepository jobRepository;
    private final JpaPagingItemReader<Movie> movieCreditJpaReader;
    private final TMDbMovieCreditProcessor processor;
    private final TMDbMovieCreditWriter writer;

    @Bean
    public Step tmdbMovieCreditStep(PlatformTransactionManager transactionManager, MovieChunkListener movieChunkListener) {
        return new StepBuilder("tmdbMovieCreditStep", jobRepository)
                .<Movie, CreditProcessorResponse>chunk(100, transactionManager)
                .reader(movieCreditJpaReader)
                .processor(processor)
                .writer(writer)
                .listener(movieChunkListener)
                .build();
    }
}
