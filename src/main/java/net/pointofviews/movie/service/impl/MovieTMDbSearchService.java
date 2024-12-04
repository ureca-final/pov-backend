package net.pointofviews.movie.service.impl;

import net.pointofviews.movie.dto.response.SearchMovieApiListResponse;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.service.MovieApiSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
public class MovieTMDbSearchService implements MovieApiSearchService {

    @Value("${TMDb.access}")
    private String TMDbApiKey;

    private final RestClient restClient;

    public MovieTMDbSearchService(RestClient.Builder restClient) {
        this.restClient = restClient.build();
    }

    @Override
    public SearchMovieApiListResponse searchMovie(String query, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.themoviedb.org")
                        .path("/3/search/movie")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                .header("Authorization", "Bearer " + TMDbApiKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError)
                .body(SearchMovieApiListResponse.class);
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
}
