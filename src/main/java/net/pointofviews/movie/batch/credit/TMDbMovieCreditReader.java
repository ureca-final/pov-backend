package net.pointofviews.movie.batch.credit;

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
public class TMDbMovieCreditReader {

    @Bean(name = "movieCreditJpaReader")
    public JpaPagingItemReader<Movie> movieCreditJpaReader(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<Movie> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setPageSize(1);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT m FROM Movie m LEFT JOIN m.casts c WHERE c IS NULL");
        reader.setPageSize(100);
        return reader;
    }
}
