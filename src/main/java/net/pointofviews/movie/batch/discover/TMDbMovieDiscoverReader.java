package net.pointofviews.movie.batch.discover;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.batch.utils.ApiRateLimiter;
import net.pointofviews.movie.dto.response.SearchMovieDiscoverApiResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class TMDbMovieDiscoverReader implements ItemReader<List<SearchMovieDiscoverApiResponse.MovieResult>> {

    private final MovieTMDbSearchService movieService;

    @Value("#{jobParameters['startDate']}")
    private String startDateStr;

    @Value("#{jobParameters['endDate']}")
    private String endDateStr;

    private LocalDate startDate;
    private LocalDate endDate;

    private final ApiRateLimiter batchRateLimiter;
    private final AtomicInteger currentPage = new AtomicInteger(1);

    private int totalPages = Integer.MAX_VALUE;

    @Override
    public List<SearchMovieDiscoverApiResponse.MovieResult> read() {
        if (startDate == null || endDate == null) {
            this.startDate = LocalDate.parse(startDateStr);
            this.endDate = LocalDate.parse(endDateStr);
        }

        int page = currentPage.getAndIncrement();

        if (page > totalPages) {
            return null;
        }

        log.info("Fetching page {} of {}", page, totalPages);
        batchRateLimiter.limit();
        SearchMovieDiscoverApiResponse response = movieService.searchDiscoverMovie(startDate, endDate, page);
        totalPages = response.total_pages();

        return response.results();
    }
}
