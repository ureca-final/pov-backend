package net.pointofviews.movie.batch.release;

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
public class TMDbMovieReleaseReader {

    private int totalItemsRead = 0;

    @Bean(name = "movieReleaseJpaReader")
    @StepScope
    public JpaPagingItemReader<Movie> movieReleaseJpaReader(EntityManagerFactory entityManagerFactory,
                                                            @Value("#{jobExecutionContext['firstMoviePk']}") Long firstMoviePk,
                                                            @Value("#{jobExecutionContext['lastMoviePk']}") Long lastMoviePk) {
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

        reader.setQueryString("SELECT m FROM Movie m WHERE m.id BETWEEN :firstMoviePk AND :lastMoviePk AND m.released IS NULL");
        reader.setParameterValues(Map.of("firstMoviePk", firstMoviePk, "lastMoviePk", lastMoviePk));
        reader.setPageSize(100);

        log.info("PK 범위로 영화 데이터를 읽습니다: firstMoviePk={}, lastMoviePk={}", firstMoviePk, lastMoviePk);
        return reader;
    }

}
