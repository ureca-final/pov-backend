package net.pointofviews.movie.batch.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JobExecutionRepository {

    private final JobExplorer jobExplorer;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2020, 1, 1);

    public LocalDate getLastExecutionDate(String jobName) {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);

        if (jobInstances.isEmpty()) {
            return DEFAULT_START_DATE;
        }

        JobInstance lastJobInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(lastJobInstance);

        if (jobExecutions.isEmpty()) {
            return DEFAULT_START_DATE;
        }

        JobExecution lastExecution = jobExecutions.get(0);

        if (lastExecution.getEndTime() == null) {
            return DEFAULT_START_DATE;
        }

        return lastExecution.getEndTime().toLocalDate();
    }
}
