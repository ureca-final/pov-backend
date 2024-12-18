package net.pointofviews.movie.batch.discover;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieGenre;
import net.pointofviews.movie.dto.response.BatchDiscoverMovieResponse;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class TMDbMovieDiscoverWriter implements ItemWriter<List<BatchDiscoverMovieResponse>> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends List<BatchDiscoverMovieResponse>> chunk) {
        List<Movie> movies = new ArrayList<>();
        List<Object[]> genreBatchArgs = new ArrayList<>();

        for (List<BatchDiscoverMovieResponse> responses : chunk) {
            for (BatchDiscoverMovieResponse response : responses) {
                movies.add(response.movie());
            }
        }

        List<Long> movieIds = batchInsertMovies(movies);

        if (!movieIds.isEmpty()) {
            savePkRangeToExecutionContext(movieIds.get(0), movieIds.get(movieIds.size() - 1));
        }

        int index = 0;
        for (List<BatchDiscoverMovieResponse> responses : chunk) {
            for (BatchDiscoverMovieResponse response : responses) {

                if (index < movieIds.size()) {
                    Long movieId = movieIds.get(index);
                    for (MovieGenre genre : response.genres()) {
                        genreBatchArgs.add(new Object[]{movieId, genre.getGenreCode()});
                    }
                    index++;
                }
            }
        }

        batchInsertGenres(genreBatchArgs);
    }

    private List<Long> batchInsertMovies(List<Movie> movies) {
        String sql = "INSERT INTO movie (title, plot, poster, backdrop, tmdb_id) VALUES (?, ?, ?, ?, ?)";
        List<Long> generatedIds = new ArrayList<>();
        List<Movie> failedMovies = new ArrayList<>();

        movies.forEach(movie -> {
            try {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, movie.getTitle());
                    ps.setString(2, movie.getPlot());
                    ps.setString(3, movie.getPoster());
                    ps.setString(4, movie.getBackdrop());
                    ps.setLong(5, movie.getTmdbId());
                    return ps;
                }, keyHolder);
                generatedIds.add(Objects.requireNonNull(keyHolder.getKey()).longValue());
            } catch (DuplicateKeyException e) {
                failedMovies.add(movie);
            }
        });

        logFailedMovies(failedMovies);

        return generatedIds;
    }

    private void logFailedMovies(List<Movie> failedMovies) {
        if (!failedMovies.isEmpty()) {
            failedMovies.forEach(movie ->
                    log.info("- {} (TMDB ID: {})", movie.getTitle(), movie.getTmdbId()));
        }
    }

    private void batchInsertGenres(List<Object[]> genres) {
        String sql = "INSERT INTO movie_genre (movie_id, genre_code) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, genres);
    }

    private void savePkRangeToExecutionContext(Long firstPk, Long lastPk) {
        StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();

        if (!jobExecutionContext.containsKey("firstMoviePk")) {
            jobExecutionContext.put("firstMoviePk", firstPk);
            log.info("첫 번째 청크의 첫 번째 영화 PK 저장: {}", firstPk);
        }

        jobExecutionContext.put("lastMoviePk", lastPk);
        log.info("현재 청크의 마지막 영화 PK 저장: {}", lastPk);
    }
}