package net.pointofviews.movie.batch.discover;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.dto.response.SearchMovieDiscoverApiResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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

    private int currentPage = 1;
    private int totalPages = Integer.MAX_VALUE;

    @Override
    public List<SearchMovieDiscoverApiResponse.MovieResult> read() {
        if (startDate == null || endDate == null) {
            this.startDate = LocalDate.parse(startDateStr);
            this.endDate = LocalDate.parse(endDateStr);
        }

        if (currentPage > totalPages) {
            return null;
        }

        log.info("Fetching page {} of {}", currentPage, totalPages);
        SearchMovieDiscoverApiResponse response = movieService.searchDiscoverMovie(startDate, endDate, currentPage);
        totalPages = response.total_pages();
        currentPage++;

        return response.results();
    }
}
