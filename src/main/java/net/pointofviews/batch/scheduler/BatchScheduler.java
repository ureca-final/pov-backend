package net.pointofviews.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Scheduled(cron = "0 0 1 * * *")
    public void adminLikedMovieManagement() {
        LocalDate start = LocalDate.now();
        log.info("관리자 좋아요 관리 배치 스케쥴링 시작: {}", start);

        try {
            Job job = jobRegistry.getJob("dailyMovieLikeJob");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runDate", start.toString())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            log.info("관리자 좋아요 관리 배치 스케쥴링 성공");
        } catch (Exception ex) {
            log.info("관리자 좋아요 관리 배치 스케쥴링 실패: {}", ex.getMessage());
        } finally {
            LocalDate end = LocalDate.now();
            log.info("관리자 좋아요 관리 배치 스케쥴링 종료: {}", end.toString());
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void trendingMovieBatch() {
        LocalDate start = LocalDate.now();
        log.info("트렌딩 영화 배치 스케쥴링 시작: {}", start);

        try {
            Job job = jobRegistry.getJob("movieTrendingJob");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runDate", start.toString())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            log.info("트렌딩 영화 배치 스케쥴링 성공");
        } catch (Exception ex) {
            log.info("트렌딩 영화 배치 스케쥴링 실패: {}", ex.getMessage());
        } finally {
            LocalDate end = LocalDate.now();
            log.info("트렌딩 영화 배치 스케쥴링 종료: {}", end.toString());
        }
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void reviewLikeSync() {
        LocalDateTime start = LocalDateTime.now();
        log.info("리뷰 좋아요 동기화 배치 시작: {}", start);

        try {
            Job job = jobRegistry.getJob("reviewLikeJob");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runDateTime", start.toString())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            log.info("리뷰 좋아요 동기화 배치 성공");
        } catch (Exception ex) {
            log.error("리뷰 좋아요 동기화 배치 실패: {}", ex.getMessage());
        } finally {
            LocalDateTime end = LocalDateTime.now();
            log.info("리뷰 좋아요 동기화 배치 종료: {}", end);
        }
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void movieLikeSync() {
        LocalDateTime start = LocalDateTime.now();
        log.info("영화 좋아요 동기화 배치 시작: {}", start);

        try {
            Job job = jobRegistry.getJob("movieLikeJob");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runDateTime", start.toString())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            log.info("영화 좋아요 동기화 배치 성공");
        } catch (Exception ex) {
            log.error("영화 좋아요 동기화 배치 실패: {}", ex.getMessage());
        } finally {
            LocalDateTime end = LocalDateTime.now();
            log.info("영화 좋아요 동기화 배치 종료: {}", end);
        }
    }
}
