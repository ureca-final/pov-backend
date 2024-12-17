package net.pointofviews.movie.batch.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.dto.response.SearchMovieTrendingApiResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDbTrendingMovieReader implements ItemReader<Integer> {

    private static final int MAX_PAGE = 500;
    private int currentPage = 1;
    private List<Integer> currentPageMovieIds = new ArrayList<>();

    private final MovieTMDbSearchService movieTMDbSearchService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Integer read() throws Exception {
        long savedMoviesCount = countTrendingMovies();

        if (savedMoviesCount >= 20) {
            log.info("[read] 배치 종료 - 트렌딩 영화 영화 저장 완료");
            return null;
        }

        if (currentPageMovieIds.isEmpty()) {

            if (currentPage > MAX_PAGE) {
                log.info("[read] 배치 종료 - 더 이상 읽을 페이지가 없습니다.");
                return null;
            }
            SearchMovieTrendingApiResponse response = movieTMDbSearchService.searchTrendingMovie("day", currentPage);

            if (response.results().isEmpty()) {
                return null;
            }

            currentPageMovieIds = response.results().stream()
                    .map(SearchMovieTrendingApiResponse.TrendingApiResponse::id)
                    .collect(Collectors.toList());

            currentPage++;

            log.info("[read] 현재 페이지 위치: {}", currentPage - 1);
        }

        return currentPageMovieIds.remove(0);
    }

    private long countTrendingMovies() {
        Set<String> keys = redisTemplate.keys("trending:*");
        return keys.size();
    }

}