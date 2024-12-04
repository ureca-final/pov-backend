package net.pointofviews.movie.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.impl.CommonCodeServiceImpl;
import net.pointofviews.common.utils.LocaleUtils;
import net.pointofviews.movie.dto.response.*;
import net.pointofviews.movie.exception.MovieException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MovieTMDbSearchService implements MovieApiSearchService {

    @Value("${TMDb.access}")
    private String TMDbApiKey;

    private final RestClient restClient;
    private final CommonCodeServiceImpl commonCodeService;

    public MovieTMDbSearchService(RestClient.Builder restClient, CommonCodeServiceImpl commonCodeService) {
        this.restClient = restClient.baseUrl("https://api.themoviedb.org/3")
                .build();
        this.commonCodeService = commonCodeService;
    }

    @Override
    public SearchMovieApiListResponse searchMovie(String query, int page) {
        SearchMovieApiListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("language", LocaleUtils.KOREAN_LANGUAGE_CODE)
                        .build())
                .header("Authorization", "Bearer " + TMDbApiKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError)
                .body(SearchMovieApiListResponse.class);

        List<SearchMovieApiResponse> results = response.results();

        transformMovieGenreResponse(results);
        return response;
    }

    @Override
    public SearchMovieDetailApiResponse searchDetailsMovie(String movieId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/")
                        .path(movieId)
                        .queryParam("language", LocaleUtils.KOREAN_LANGUAGE_CODE)
                        .build())
                .header("Authorization", "Bearer " + TMDbApiKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError)
                .body(SearchMovieDetailApiResponse.class);
    }

    @Override
    public SearchCreditApiResponse searchLimit10Credit(String movieId) {
        SearchCreditApiResponse response = searchCredit(movieId);
        List<SearchCreditApiResponse.CastResponse> limitCastList = response.cast()
                .subList(0, Math.min(response.cast().size(), 10));
        List<SearchCreditApiResponse.CrewResponse> directors = response.crew().stream()
                .filter(crew -> "director".equalsIgnoreCase(crew.job()))
                .toList();

        return new SearchCreditApiResponse(response.id(), limitCastList, directors);
    }

    @Override
    public SearchReleaseApiResponse searchReleaseDate(String movieId) {
        SearchReleaseApiResponse response = searchApiReleaseDate(movieId);

        SearchReleaseApiResponse.Result bestResult = response.results().stream()
                .min(Comparator.comparingInt(result -> {
                    if ("kr".equalsIgnoreCase(result.iso_3166_1())) {
                        return 1;
                    } else if ("us".equalsIgnoreCase(result.iso_3166_1())) {
                        return 2;
                    } else {
                        return 3;
                    }
                }))
                .orElse(null);

        if (bestResult == null) {
            return new SearchReleaseApiResponse(response.id(), List.of());
        }

        return new SearchReleaseApiResponse(
                response.id(),
                List.of(new SearchReleaseApiResponse.Result(bestResult.iso_3166_1(), bestResult.release_dates()))
        );
    }


    private SearchReleaseApiResponse searchApiReleaseDate(String movieId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/")
                        .path(movieId)
                        .path("/release_dates")
                        .build())
                .header("Authorization", "Bearer " + TMDbApiKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError)
                .body(SearchReleaseApiResponse.class);
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) {
        try {
            String messages = new String(response.getBody().readAllBytes());

            String startKeyword = "\"status_message\":\"";
            String endKeyword = "\"";
            String message = StringUtils.substringBetween(messages, startKeyword, endKeyword);

            throw MovieException.tmdbBadRequest(message);
        } catch (IOException e) {
            throw new RuntimeException("외부 응답 읽기 실패", e);
        }
    }

    private void transformMovieGenreResponse(List<SearchMovieApiResponse> results) {
        for (SearchMovieApiResponse result : results) {
            List<String> genreId = result.genre_ids();

            List<String> stringGenre = new ArrayList<>();
            for (String s : genreId) {
                stringGenre.add(commonCodeService.convertCommonCodeNameToName(s, CodeGroupEnum.MOVIE_GENRE));
            }

            genreId.clear();
            genreId.addAll(stringGenre);
        }
    }

    private SearchCreditApiResponse searchCredit(String movieId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/")
                        .path(movieId)
                        .path("/credits")
                        .queryParam("language", LocaleUtils.KOREAN_LANGUAGE_CODE)
                        .build())
                .header("Authorization", "Bearer " + TMDbApiKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError)
                .body(SearchCreditApiResponse.class);
    }
}
