package net.pointofviews.movie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.pointofviews.movie.dto.response.SearchMovieApiListResponse;
import net.pointofviews.movie.dto.response.SearchMovieApiResponse;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
@RestClientTest(MovieTMDbSearchService.class)
class MovieTMDbSearchServiceTest {

    @Autowired
    private MovieTMDbSearchService movieTMDbSearchService;

    @Autowired
    private MockRestServiceServer server;

    @Nested
    class SearchMovie {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void TMDb_검색_성공() {
                // given
                SearchMovieApiResponse mockResponse = new SearchMovieApiResponse(false, null, 1, null, null, "Inception");
                SearchMovieApiListResponse mockResponseList = new SearchMovieApiListResponse(1, 10, 100, List.of(mockResponse));

                ObjectMapper objectMapper = new ObjectMapper();
                String validJsonResponse = objectMapper.writeValueAsString(mockResponseList);
                String query = "inception";
                int page = 1;
                URI uri = URI.create("https://api.themoviedb.org/3/search/movie?query=" + query + "&page=" + page + "&language=ko-KR");

                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));

                // when
                SearchMovieApiListResponse result = movieTMDbSearchService.searchMovie(query, page);

                // then
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.results()).hasSize(1);
                Assertions.assertThat(result.results().get(0).title()).isEqualTo("Inception");
            }
        }

        @Nested
        class Failure {

            @Test
            void 페이지_0_이하_요청_MovieException_예외발생() {
                // given
                String query = "inception";
                int wrongPage = 0;
                URI uri = URI.create("https://api.themoviedb.org/3/search/movie?query=" + query + "&page=" + wrongPage + "&language=ko-KR");
                String errorResponse = "{\"success\":false,\"status_code\":22,\"status_message\":\"Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.\"}";
                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withBadRequest()
                        .body(errorResponse)
                        .contentType(MediaType.APPLICATION_JSON));

                // when && then
                Assertions.assertThatThrownBy(
                                () -> movieTMDbSearchService.searchMovie(query, wrongPage)
                        ).hasMessage("Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.")
                        .isInstanceOf(MovieException.class);
            }

            @Test
            void 페이지_500_초과_요청_MovieException_예외발생() {
                // given
                String query = "inception";
                int wrongPage = 501;
                URI uri = URI.create("https://api.themoviedb.org/3/search/movie?query=" + query + "&page=" + wrongPage + "&language=ko-KR");
                String errorResponse = "{\"success\":false,\"status_code\":22,\"status_message\":\"Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.\"}";
                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withBadRequest()
                        .body(errorResponse)
                        .contentType(MediaType.APPLICATION_JSON));

                // when && then
                Assertions.assertThatThrownBy(
                                () -> movieTMDbSearchService.searchMovie(query, wrongPage)
                        ).hasMessage("Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.")
                        .isInstanceOf(MovieException.class);
            }

            @Test
            void 클라이언트_응답_읽기_IOException_발생() {
                // given
                String query = "inception";
                int wrongPage = 0;
                URI uri = URI.create("https://api.themoviedb.org/3/search/movie?query=" + query + "&page=" + wrongPage + "&language=ko-KR");

                MockClientHttpResponse mockResponse = new MockClientHttpResponse(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), 400) {
                    @Override
                    public java.io.InputStream getBody() throws IOException {
                        throw new IOException("강제 IOException");
                    }
                };

                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(request -> mockResponse);

                // when && then
                Assertions.assertThatThrownBy(
                                () -> movieTMDbSearchService.searchMovie(query, wrongPage)
                        )
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("외부 응답 읽기 실패");
            }
        }
    }

}
