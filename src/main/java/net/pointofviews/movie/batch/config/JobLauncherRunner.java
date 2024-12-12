package net.pointofviews.movie.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobLauncherRunner implements ApplicationRunner {
    private final JobLauncher jobLauncher;
    private final Job tmdbMovieDiscoverJob;
    private final JobRepository jobRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String startDate = "2020-01-01";
        String endDate = "2024-12-31";
        JobParameters parameters = new JobParametersBuilder()
                .addString("startDate", startDate)
                .addString("endDate", endDate)
                .toJobParameters();

        if (jobRepository.isJobInstanceExists(tmdbMovieDiscoverJob.getName(), parameters)) {
            log.info("이미 실행된 JobInstance가 있습니다. Job을 실행하지 않습니다.");
            return;
        }

        log.info("TMDb 영화 배치 잡 실행 {} {} ~ {}", tmdbMovieDiscoverJob.getName(), startDate, endDate);
        jobLauncher.run(tmdbMovieDiscoverJob, parameters);
    }
}