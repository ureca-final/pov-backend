package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.common.service.RedisService;
import net.pointofviews.country.domain.Country;
import net.pointofviews.movie.domain.*;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.request.PutMovieRequest;
import net.pointofviews.movie.dto.response.MovieListResponse;
import net.pointofviews.movie.dto.response.MovieResponse;
import net.pointofviews.movie.dto.response.MovieTrendingListResponse;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieService;
import net.pointofviews.people.domain.People;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.pointofviews.movie.dto.request.CreateMovieRequest.SearchCreditApiRequest.CastRequest;
import static net.pointofviews.movie.dto.request.CreateMovieRequest.SearchCreditApiRequest.CrewRequest;
import static net.pointofviews.movie.exception.MovieException.duplicateMovie;
import static net.pointofviews.movie.exception.MovieException.movieNotFound;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final RedisService redisService;
    private final MovieRepository movieRepository;
    private final CommonCodeService commonCodeService;
    private final MovieCountryServiceImpl movieCountryServiceImpl;
    private final MoviePeopleServiceImpl moviePeopleServiceImpl;

    @Override
    public void saveMovie(CreateMovieRequest request) {
        if (movieRepository.existsByTmdbId(request.tmdbId())) {
            throw duplicateMovie(request.tmdbId());
        }

        Movie movie = request.toMovieEntity();

        List<MovieGenre> movieGenres = convertStringsToMovieGenre(request.genres());
        movieGenres.forEach(movie::addGenre);

        List<MovieCast> casts = processMoviePeoples(
                request.peoples().cast(),
                CastRequest::toPeopleEntity,
                CastRequest::toMovieCastEntity,
                People::addCast
        );
        casts.forEach(movie::addCast);

        List<MovieCrew> crews = processMoviePeoples(
                request.peoples().crew(),
                CrewRequest::toPeopleEntity,
                CrewRequest::toMovieCrewEntity,
                People::addCrew
        );
        crews.forEach(movie::addCrew);

        List<MovieCountry> movieCountries = processMovieCountries(request.originCountries());
        movieCountries.forEach(movie::addCountry);

        movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw movieNotFound(movieId);
        }

        movieRepository.deleteById(movieId);
    }

    @Override
    public void updateMovie(Long movieId, PutMovieRequest request) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> movieNotFound(movieId));

        List<MovieGenre> genres = convertStringsToMovieGenre(request.genre());
        genres.forEach(movie::addGenre);

        List<MovieCountry> countries = processMovieCountries(request.country());
        countries.forEach(movie::addCountry);

        List<MovieCast> casts = processMoviePeoples(
                request.peoples().cast(),
                PutMovieRequest.UpdateMoviePeopleRequest.CastRequest::toPeopleEntity,
                PutMovieRequest.UpdateMoviePeopleRequest.CastRequest::toMovieCastEntity,
                People::addCast
        );
        casts.forEach(cast -> cast.updateMovie(movie));

        List<MovieCrew> crews = processMoviePeoples(
                request.peoples().crew(),
                PutMovieRequest.UpdateMoviePeopleRequest.CrewRequest::toPeopleEntity,
                PutMovieRequest.UpdateMoviePeopleRequest.CrewRequest::toMovieCrewEntity,
                People::addCrew
        );
        crews.forEach(crew -> crew.updateMovie(movie));

        movie.updateMovie(request, casts, crews, countries, genres);
    }

    @Override
    public MovieListResponse readMovies(UUID memberId, Pageable pageable) {
        Slice<MovieResponse> responses = movieRepository.findAllMovies(memberId, pageable)
                .map(row -> new MovieResponse(
                        ((Number) row[0]).longValue(), // id
                        (String) row[1],                 // title
                        (String) row[2],                 // poster
                        (LocalDate) row[3],                   // released
                        row[4] != null && (row[4] instanceof Number ? ((Number) row[4]).intValue() == 1 : (Boolean) row[4]), // isLiked
                        row[5] != null ? ((Number) row[5]).longValue() : 0L,  // movieLikeCount
                        row[6] != null ? ((Number) row[6]).longValue() : 0L   // movieReviewCount
                ));
        return new MovieListResponse(responses);
    }

    @Override
    public MovieTrendingListResponse getTrendingMovies(UUID memberId) {
        List<Long> trendingMovieId = redisService.getSetMembers("trending").stream().map(Long::parseLong).toList();

        return new MovieTrendingListResponse(movieRepository.findAllTrendingMovie(trendingMovieId, memberId));
    }

    private List<MovieGenre> convertStringsToMovieGenre(List<String> stringGenres) {
        return stringGenres.stream()
                .map(genre -> {
                    String genreCode = commonCodeService.convertCommonCodeDescriptionToCode(genre, CodeGroupEnum.MOVIE_GENRE);
                    return MovieGenre.builder().genreCode(genreCode).build();
                })
                .toList();
    }

    private <T, E> List<E> processMoviePeoples(
            List<T> requests,
            Function<T, People> toPeople,
            Function<T, E> toMovieEntity,
            BiConsumer<People, E> associateEntity
    ) {
        List<E> entities = new ArrayList<>();
        for (T request : requests) {
            People people = toPeople.apply(request);
            people = moviePeopleServiceImpl.savePeopleIfNotExists(people);
            E entity = toMovieEntity.apply(request);
            associateEntity.accept(people, entity);
            entities.add(entity);
        }

        return entities;
    }

    private List<MovieCountry> processMovieCountries(List<String> countryRequests) {
        return countryRequests.stream()
                .map(Country::new)
                .map(movieCountryServiceImpl::saveMovieCountries)
                .map(MovieCountry::new)
                .toList();
    }
}
