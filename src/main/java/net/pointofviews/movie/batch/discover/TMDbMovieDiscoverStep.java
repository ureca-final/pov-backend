package net.pointofviews.movie.batch.discover;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.dto.response.BatchDiscoverMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieDiscoverApiResponse;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TMDbMovieDiscoverStep {

    private final JobRepository jobRepository;
    private final TMDbMovieDiscoverReader reader;
    private final TMDbMovieDiscoverProcessor processor;
    private final TMDbMovieDiscoverWriter writer;

    @Bean
    public Step tmdbMovieDiscoverStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("tmdbMovieDiscoverStep", jobRepository)
                .<List<SearchMovieDiscoverApiResponse.MovieResult>, List<BatchDiscoverMovieResponse>>chunk(20, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
