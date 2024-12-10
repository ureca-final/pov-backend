package net.pointofviews.movie.batch.release;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.Movie;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbMovieReleaseReader {

    @Bean(name = "movieReleaseJpaReader")
    public JpaPagingItemReader<Movie> movieReleaseJpaReader(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<Movie> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setPageSize(1);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT m FROM Movie m WHERE m.released IS NULL");
        reader.setPageSize(100);
        return reader;
    }

}
