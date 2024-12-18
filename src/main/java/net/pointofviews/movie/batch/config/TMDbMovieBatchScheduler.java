package net.pointofviews.movie.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.batch.repository.JobExecutionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "TMDbMovieBatch")
public class TMDbMovieBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job fetchMovieJob;
    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void runBatchJob() {
        try {
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

            if (jobRepository.isJobInstanceExists(fetchMovieJob.getName(), parameters)) {
                log.info("이미 실행된 JobInstance가 있습니다. Job을 실행하지 않습니다.");
                return;
            }

            log.info("TMDb 영화 배치 잡 실행 {} {} ~ {}", fetchMovieJob.getName(), startDate, endDate);
            jobLauncher.run(fetchMovieJob, parameters);

        } catch (Exception e) {
            log.error("배치 잡 실행 중 오류 발생", e);
        }
    }

    private String calculateStartDate() {
        LocalDate lastExecutedTime = jobExecutionRepository.getLastExecutionDate(fetchMovieJob.getName());
        return lastExecutedTime.toString();
    }

    private String calculateEndDate() {
        return LocalDate.now().toString();
    }
}
