package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.dto.response.AdminSearchMovieListResponse;
import net.pointofviews.movie.dto.response.AdminSearchMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import net.pointofviews.movie.dto.response.SearchMovieResponse;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieSearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MovieSearchServiceImpl implements MovieSearchService {

    private final MovieRepository movieRepository;

    @Override
    public SearchMovieListResponse searchMovies(String query, Pageable pageable) {

        Slice<SearchMovieResponse> responses =  movieRepository.searchMoviesByTitleOrPeople(query, pageable)
        .map(row -> new SearchMovieResponse(
                ((Number) row[0]).longValue(),  // id
                (String) row[1],               // title
                (String) row[2],               // poster
                (Date) row[3],               // released
                row[4] != null ? ((Number) row[4]).intValue() : 0,  // likeCount (null이면 0)
                row[5] != null ? ((Number) row[5]).intValue() : 0   // reviewCount (null이면 0)
        ));

        return new SearchMovieListResponse(responses);
    }

    @Override
    public AdminSearchMovieListResponse adminSearchMovies(String query, Pageable pageable) {
        Slice<AdminSearchMovieResponse> movieResponses = movieRepository.adminSearchMovies(query, pageable)
                .map(row -> new AdminSearchMovieResponse(
                        ((Number) row[0]).longValue(),  // id
                        (String) row[1],                // title
                        ((java.sql.Date) row[2]).toLocalDate() // released
                ));

        return new AdminSearchMovieListResponse(movieResponses);
    }
}
