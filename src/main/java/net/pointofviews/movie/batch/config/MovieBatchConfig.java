package net.pointofviews.movie.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
public class MovieBatchConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job fetchMovieJob(Step tmdbMovieDiscoverStep, Flow parallelFlow) {
        return new JobBuilder("fetchMovieJob", jobRepository)
                .start(tmdbMovieDiscoverStep)
                .next(parallelStep(parallelFlow))
                .build();
    }

    @Bean
    public Step parallelStep(Flow parallelFlow) {
        return new StepBuilder("parallelStep", jobRepository)
                .flow(parallelFlow)
                .build();
    }

    @Bean
    public Flow parallelFlow(Step tmdbMovieReleaseStep, Step tmdbMovieCountryStep,
                             Step tmdbMovieCreditStep, Step tmdbMovieImageStep, TaskExecutor tmdbTaskExecutor) {
        return new FlowBuilder<Flow>("parallelFlow")
                .split(tmdbTaskExecutor)
                .add(
                        new FlowBuilder<Flow>("releaseFlow").start(tmdbMovieReleaseStep).build(),
                        new FlowBuilder<Flow>("countryFlow").start(tmdbMovieCountryStep).build(),
                        new FlowBuilder<Flow>("creditFlow").start(tmdbMovieCreditStep).build(),
                        new FlowBuilder<Flow>("imageFlow").start(tmdbMovieImageStep).build()
                )
                .build();
    }
}
