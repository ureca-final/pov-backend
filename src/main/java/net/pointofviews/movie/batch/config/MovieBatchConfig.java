package net.pointofviews.movie.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MovieBatchConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job tmdbMovieDiscoverJob(Step tmdbMovieDiscoverStep, Step tmdbMovieReleaseStep, Step tmdbMovieCountryStep) {
        return new JobBuilder("fetchMovieJob", jobRepository)
                .start(tmdbMovieDiscoverStep)
                .next(tmdbMovieReleaseStep)
                .next(tmdbMovieCountryStep)
                .build();
    }
}
