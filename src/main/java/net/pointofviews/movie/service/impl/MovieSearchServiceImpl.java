package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.*;
import net.pointofviews.movie.dto.response.*;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieContentRepository;
import net.pointofviews.movie.repository.MovieLikeCountRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieSearchService;
import net.pointofviews.review.dto.ReviewDetailsWithLikeCountDto;
import net.pointofviews.review.dto.ReviewPreferenceCountDto;
import net.pointofviews.review.repository.ReviewRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovieSearchServiceImpl implements MovieSearchService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final CommonCodeService commonCodeService;
    private final MovieContentRepository movieContentRepository;
    private final MovieLikeCountRepository movieLikeCountRepository;

    @Override
    public SearchMovieListResponse searchMovies(String query, Member loginMember, Pageable pageable) {

        Slice<SearchMovieResponse> responses = movieRepository.searchMoviesByTitleOrPeople(query, loginMember.getId(), pageable)
                .map(row -> new SearchMovieResponse(
                        ((Number) row[0]).longValue(),    // id
                        (String) row[1],                 // title
                        (String) row[2],                 // poster
                        (Date) row[3],                   // released
                        row[4] instanceof Number ? ((Number) row[4]).intValue() == 1 : (Boolean) row[4], // isLiked
                        row[5] != null ? ((Number) row[5]).longValue() : 0L,  // movieLikeCount
                        row[6] != null ? ((Number) row[6]).longValue() : 0L   // movieReviewCount
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

    @Override
    public ReadDetailMovieResponse readDetailMovie(Long movieId) {
        Movie movieDetails = movieRepository.findMovieWithDetailsById(movieId)
                .orElseThrow(() -> MovieException.movieNotFound(movieId));

        List<String> movieGenre = movieDetails.getGenres().stream()
                .map(g -> commonCodeService.convertCommonCodeToName(g.getGenreCode(), CodeGroupEnum.MOVIE_GENRE))
                .toList();

        Long movieLikeCount = movieLikeCountRepository.findById(movieId)
                .map(MovieLikeCount::getLikeCount)
                .orElse(0L);

        Set<MovieCrew> crews = movieDetails.getCrews();
        List<ReadDetailMovieResponse.ReadMovieCrewResponse> crewResponses = crews.stream()
                .map(ReadDetailMovieResponse.ReadMovieCrewResponse::of)
                .toList();

        Set<MovieCast> casts = movieDetails.getCasts();
        List<ReadDetailMovieResponse.ReadMovieCastResponse> castResponses = casts.stream()
                .map(ReadDetailMovieResponse.ReadMovieCastResponse::of)
                .toList();

        List<ReviewPreferenceCountDto> reviewPreferenceCountDtoList = reviewRepository.countReviewPreferenceByMovieId(movieId);
        List<String> countries = movieDetails.getCountries().stream().map(c -> c.getCountry().getName()).toList();

        List<MovieContent> movieContents = movieContentRepository.findAllByMovieId(movieId);
        List<String> images = movieContents.stream()
                .filter(movieContent -> movieContent.getContentType().equals(MovieContentType.IMAGE))
                .map(MovieContent::getContent)
                .toList();
        List<String> videos = movieContents.stream().
                filter(movieContent -> movieContent.getContentType().equals(MovieContentType.YOUTUBE))
                .map(MovieContent::getContent)
                .toList();

        List<ReviewDetailsWithLikeCountDto> reviews = reviewRepository.findTop3ByMovieIdOrderByReviewLikeCountDesc(movieId, PageRequest.of(0, 3));

        return new ReadDetailMovieResponse(
                movieDetails.getTitle(),
                movieDetails.getReleased(),
                movieGenre,
                movieLikeCount,
                reviewPreferenceCountDtoList,
                movieDetails.getPlot(),
                crewResponses,
                castResponses,
                movieDetails.getPoster(),
                movieDetails.getBackdrop(),
                countries,
                images,
                videos,
                reviews
        );
    }
}
