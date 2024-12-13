package net.pointofviews.movie.batch.country;

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
public class TMDbMovieCountryReader {

    private int totalItemsRead = 0;

    @Bean(name = "movieCountryJpaReader")
    public JpaPagingItemReader<Movie> movieCountryJpaReader(EntityManagerFactory entityManagerFactory) {
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
                    SELECT m FROM Movie m WHERE m.countries IS EMPTY
                """);
        reader.setPageSize(100);
        return reader;
    }
}
