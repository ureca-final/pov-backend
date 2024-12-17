package net.pointofviews.movie.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.batch.repository.JobExecutionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobLauncherRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job tmdbMovieDiscoverJob;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobRepository jobRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String startDate = calculateStartDate();
        String endDate = calculateEndDate();

        if(startDate.equals(endDate)) {
            log.info("금일 실행 된 JobInstance가 존재합니다. Job을 실행하지 않습니다.");
            return;
        }

        JobParameters parameters = new JobParametersBuilder()
                .addString("startDate", startDate)
                .addString("endDate", endDate)
                .toJobParameters();

        if (jobRepository.isJobInstanceExists(tmdbMovieDiscoverJob.getName(), parameters)) {
            log.info("이미 동일한 파라미터로 실행된 JobInstance가 존재합니다. Job을 실행하지 않습니다.");
            return;
        }

        log.info("TMDb 영화 배치 잡 실행 {} {} ~ {}", tmdbMovieDiscoverJob.getName(), startDate, endDate);
        jobLauncher.run(tmdbMovieDiscoverJob, parameters);
    }

    private String calculateStartDate() {
        LocalDate lastExecutedTime = jobExecutionRepository.getLastExecutionDate(tmdbMovieDiscoverJob.getName());
        return lastExecutedTime.toString();
    }

    private String calculateEndDate() {
        return LocalDate.now().toString();
    }
}
