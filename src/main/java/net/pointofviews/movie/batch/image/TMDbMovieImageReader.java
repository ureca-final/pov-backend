package net.pointofviews.movie.batch.image;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbMovieImageReader {

    private int totalItemsRead = 0;

    @Bean(name = "movieImageJpaReader")
    @StepScope
    public JpaPagingItemReader<Movie> movieImageJpaReader(EntityManagerFactory entityManagerFactory,
                                                          @Value("#{jobExecutionContext['firstMoviePk']}") Long firstMoviePk,
                                                          @Value("#{jobExecutionContext['lastMoviePk']}") Long lastMoviePk,
                                                          @Value("#{stepExecutionContext['lastProcessedMovieId']}") Long lastProcessedMovieId) {

        if (lastProcessedMovieId != null && lastProcessedMovieId >= lastMoviePk) {
            log.info("lastProcessedMovieId ({})가 lastMoviePk ({})를 초과했습니다. 스텝 종료를 요청합니다.", lastProcessedMovieId, lastMoviePk);
            return null;
        }

        JpaPagingItemReader<Movie> reader = new JpaPagingItemReader<>() {
            @Override
            protected Movie doRead() throws Exception {
                Movie movie = super.doRead();
                if (movie != null) {
                    totalItemsRead++;
                } else {
                    log.info("Total items read: {}", totalItemsRead);
                }
                return movie;
            }

            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setEntityManagerFactory(entityManagerFactory);

        reader.setQueryString("""
                SELECT m FROM Movie m
                LEFT JOIN MovieContent mc ON mc.movie.id = m.id
                WHERE mc.id IS NULL
                AND m.id BETWEEN :firstMoviePk AND :lastMoviePk
                AND m.id > :lastProcessedMovieId
                """);

        reader.setParameterValues(Map.of(
                "firstMoviePk", firstMoviePk,
                "lastMoviePk", lastMoviePk,
                "lastProcessedMovieId", (lastProcessedMovieId != null) ? lastProcessedMovieId : 0L
        ));
        reader.setPageSize(100);

        log.info("PK 범위로 영화 데이터를 읽습니다: firstMoviePk={}, lastMoviePk={}, lastProcessedMovieId={}", firstMoviePk, lastMoviePk, lastProcessedMovieId);
        return reader;
    }
}
