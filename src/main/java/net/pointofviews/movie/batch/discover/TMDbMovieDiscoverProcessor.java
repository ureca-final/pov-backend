package net.pointofviews.movie.batch.discover;

import io.jsonwebtoken.lang.Objects;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieGenre;
import net.pointofviews.movie.dto.response.BatchDiscoverMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieDiscoverApiResponse;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TMDbMovieDiscoverProcessor implements ItemProcessor<List<SearchMovieDiscoverApiResponse.MovieResult>, List<BatchDiscoverMovieResponse>> {

    private static final String IMAGE_PATH = "https://image.tmdb.org/t/p/w500";
    private final CommonCodeService commonCodeService;

    @Override
    public List<BatchDiscoverMovieResponse> process(List<SearchMovieDiscoverApiResponse.MovieResult> results) {
        List<BatchDiscoverMovieResponse> processedResults = new ArrayList<>();

        for (SearchMovieDiscoverApiResponse.MovieResult result : results) {
            String title = Objects.isEmpty(result.title()) ? result.original_title() : result.title();
            List<Integer> genreIds = result.genre_ids();

            Movie movie = Movie.builder()
                    .tmdbId(result.id())
                    .title(title)
                    .backdrop(IMAGE_PATH + result.backdrop_path())
                    .poster(IMAGE_PATH + result.poster_path())
                    .plot(result.overview())
                    .build();

            List<MovieGenre> genres = new ArrayList<>();
            for (Integer genreId : genreIds) {
                String genreCode = commonCodeService.convertCommonCodeNameToCommonCode(genreId.toString(), CodeGroupEnum.MOVIE_GENRE);
                MovieGenre genre = MovieGenre.builder()
                        .genreCode(genreCode)
                        .movie(movie)
                        .build();
                genres.add(genre);
            }

            processedResults.add(new BatchDiscoverMovieResponse(movie, genres));
        }

        return processedResults;
    }
}