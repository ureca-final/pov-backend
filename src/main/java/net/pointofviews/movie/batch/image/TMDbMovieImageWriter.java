package net.pointofviews.movie.batch.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.batch.dto.MovieContentsDto;
import net.pointofviews.movie.domain.MovieContent;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbMovieImageWriter implements ItemWriter<MovieContentsDto>, StepExecutionListener {

    private final JdbcTemplate jdbcTemplate;
    private Long lastProcessedMovieId = 0L;

    @Override
    public void write(Chunk<? extends MovieContentsDto> chunk) {
        List<MovieContent> allMovieContents = chunk.getItems().stream()
                .flatMap(dto -> dto.movieContents().stream())
                .toList();

        batchInsertMovieContents(allMovieContents);

        updateLastProcessedMovieId(chunk);
    }

    private void updateLastProcessedMovieId(Chunk<? extends MovieContentsDto> chunk) {
        MovieContentsDto lastDto = chunk.getItems().get(chunk.getItems().size() - 1);
        if (!lastDto.movieContents().isEmpty()) {
            lastProcessedMovieId = lastDto.movieContents()
                    .get(lastDto.movieContents().size() - 1)
                    .getMovie()
                    .getId();
        }
    }

    private void batchInsertMovieContents(List<MovieContent> movieContents) {
        if (movieContents.isEmpty()) {
            log.info("삽입할 MovieContent가 없습니다.");
            return;
        }

        String sql = "INSERT INTO movie_content (movie_id, content, content_type) VALUES (?, ?, ?)";

        List<Object[]> batchArgs = movieContents.stream()
                .map(content -> new Object[]{
                        content.getMovie().getId(),
                        content.getContent(),
                        content.getContentType().name()
                })
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);

        log.info("총 {}개의 MovieContent를 배치로 저장했습니다.", batchArgs.size());
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("TMDbMovieImageWriter 시작");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution()
                .getExecutionContext()
                .put("lastProcessedMovieId", lastProcessedMovieId);

        log.info("스텝 종료: 마지막으로 처리된 movie ID: {}", lastProcessedMovieId);
        return ExitStatus.COMPLETED;
    }
}
