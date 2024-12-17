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
public class MovieTrendingConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job movieTrendingJob(Step trendingMovieStep) {
        return new JobBuilder("movieTrendingJob", jobRepository)
                .start(trendingMovieStep)
                .build();
    }
}
