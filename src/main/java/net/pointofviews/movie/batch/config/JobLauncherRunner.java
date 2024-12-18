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
    private final Job fetchMovieJob;
    private final Job movieTrendingJob;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobRepository jobRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("서버 시작 시 최초 1회 실행");

        if (runJob(fetchMovieJob)) {
            runJob(movieTrendingJob);
        } else {
            log.warn("첫 번째 Job이 실패했거나 실행 중단되어 두 번째 Job은 실행되지 않습니다.");
        }
    }

    private boolean runJob(Job job) {
        String startDate = calculateStartDate(job.getName());
        String endDate = calculateEndDate();

        if (startDate.equals(endDate) || isJobAlreadyExecuted(job, startDate, endDate)) {
            log.info("{} Job은 이미 실행된 기록이 있습니다.", job.getName());
            return true;
        }

        try {
            JobParameters parameters = new JobParametersBuilder()
                    .addString("startDate", startDate)
                    .addString("endDate", endDate)
                    .toJobParameters();

            log.info("Job 실행: {} ({} ~ {})", job.getName(), startDate, endDate);
            jobLauncher.run(job, parameters);
            return true;
        } catch (Exception e) {
            log.error("{} Job 실행 중 오류 발생", job.getName(), e);
            return false;
        }
    }

    private boolean isJobAlreadyExecuted(Job job, String startDate, String endDate) {
        JobParameters parameters = new JobParametersBuilder()
                .addString("startDate", startDate)
                .addString("endDate", endDate)
                .toJobParameters();

        try {
            return jobRepository.isJobInstanceExists(job.getName(), parameters);
        } catch (Exception e) {
            log.error("{} Job 기록 확인 중 오류 발생", job.getName(), e);
            return false;
        }
    }

    private String calculateStartDate(String jobName) {
        LocalDate lastExecutedTime = jobExecutionRepository.getLastExecutionDate(jobName);
        return lastExecutedTime.toString();
    }

    private String calculateEndDate() {
        return LocalDate.now().toString();
    }
}
