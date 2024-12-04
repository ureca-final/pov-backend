package net.pointofviews.movie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.impl.CommonCodeServiceImpl;
import net.pointofviews.common.utils.ISOCodeToKoreanConverter;
import net.pointofviews.movie.dto.response.SearchCreditApiResponse;
import net.pointofviews.movie.dto.response.SearchMovieApiListResponse;
import net.pointofviews.movie.dto.response.SearchMovieApiResponse;
import net.pointofviews.movie.dto.response.SearchMovieDetailApiResponse;
import net.pointofviews.movie.exception.MovieException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
@RestClientTest(MovieTMDbSearchService.class)
class MovieTMDbSearchServiceTest {

    @Autowired
    private MovieTMDbSearchService movieTMDbSearchService;

    @MockBean
    private CommonCodeServiceImpl commonCodeService;

    @Autowired
    private MockRestServiceServer server;

    @Nested
    class SearchLimit10Credit {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void 영화_크레딧_배우_최대_10명과_갑독_찾기() {
                // given
                String movieId = "27205";
                SearchCreditApiResponse mockResponse = createMockSearchCreditResponse();
                ObjectMapper objectMapper = new ObjectMapper();
                String validJsonResponse = objectMapper.writeValueAsString(mockResponse);
                String koreanIsoCode = ISOCodeToKoreanConverter.KOREAN_LANGUAGE_CODE;
                URI uri = URI.create("https://api.themoviedb.org/3/movie/" + movieId + "/credits?language=" + koreanIsoCode);

                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));

                // when
                SearchCreditApiResponse result = movieTMDbSearchService.searchLimit10Credit(movieId);

                // then
                assertThat(result.cast()).hasSize(10);
                assertThat(result.crew()).hasSize(1);
                assertThat(result.crew().get(0).job()).isEqualToIgnoringCase("director");
                assertThat(result.id()).isEqualTo(mockResponse.id());
            }
        }

        private SearchCreditApiResponse createMockSearchCreditResponse() {
            List<SearchCreditApiResponse.CastResponse> castList = List.of(
                    new SearchCreditApiResponse.CastResponse(2, 1, "Actor1", "Actor1", "/profile1.jpg", 1, "Character1", 1),
                    new SearchCreditApiResponse.CastResponse(2, 2, "Actor2", "Actor2", "/profile2.jpg", 2, "Character2", 2),
                    new SearchCreditApiResponse.CastResponse(2, 3, "Actor3", "Actor3", "/profile3.jpg", 3, "Character3", 3),
                    new SearchCreditApiResponse.CastResponse(2, 4, "Actor4", "Actor4", "/profile4.jpg", 4, "Character4", 4),
                    new SearchCreditApiResponse.CastResponse(2, 5, "Actor5", "Actor5", "/profile5.jpg", 5, "Character5", 5),
                    new SearchCreditApiResponse.CastResponse(2, 6, "Actor6", "Actor6", "/profile6.jpg", 6, "Character6", 6),
                    new SearchCreditApiResponse.CastResponse(2, 7, "Actor7", "Actor7", "/profile7.jpg", 7, "Character7", 7),
                    new SearchCreditApiResponse.CastResponse(2, 8, "Actor8", "Actor8", "/profile8.jpg", 8, "Character8", 8),
                    new SearchCreditApiResponse.CastResponse(2, 9, "Actor9", "Actor9", "/profile9.jpg", 9, "Character9", 9),
                    new SearchCreditApiResponse.CastResponse(2, 10, "Actor10", "Actor10", "/profile10.jpg", 10, "Character10", 10),
                    new SearchCreditApiResponse.CastResponse(2, 11, "Actor11", "Actor11", "/profile11.jpg", 11, "Character11", 11)
            );

            List<SearchCreditApiResponse.CrewResponse> crewList = List.of(
                    new SearchCreditApiResponse.CrewResponse(2, 100, "Director1", "Director1", 90.0, "/director1.jpg", "Directing", "Director"),
                    new SearchCreditApiResponse.CrewResponse(2, 101, "Producer1", "Producer1", 80.0, "/producer1.jpg", "Producing", "Producer")
            );

            return new SearchCreditApiResponse(27205, castList, crewList);
        }
    }

    @Nested
    class SearchCredit {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void 영화_크레딧_검색() {
                // given
                SearchCreditApiResponse mockResponseList = mock(SearchCreditApiResponse.class);
                ObjectMapper objectMapper = new ObjectMapper();
                String validJsonResponse = objectMapper.writeValueAsString(mockResponseList);
                String movieId = "27205";
                String koreanIsoCode = ISOCodeToKoreanConverter.KOREAN_LANGUAGE_CODE;

                URI uri = URI.create("https://api.themoviedb.org/3/movie/" + movieId + "/credits?language=" + koreanIsoCode);

                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));

                // when
                SearchCreditApiResponse result = movieTMDbSearchService.searchCredit(movieId);

                // then
                assertThat(result).isNotNull();
                assertThat(result.cast()).hasSize(0);
            }
        }
    }

    @Nested
    class SearchMovie {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void TMDb_검색_성공() {
                // given
                List<String> genreList = List.of("28");
                String convertedGenreCode = "액션";
                SearchMovieApiResponse mockResponse = new SearchMovieApiResponse(false, genreList, 1, null, null, "Inception");
                SearchMovieApiListResponse mockResponseList = new SearchMovieApiListResponse(1, 10, 100, List.of(mockResponse));

                ObjectMapper objectMapper = new ObjectMapper();
                String validJsonResponse = objectMapper.writeValueAsString(mockResponseList);
                String query = "inception";
                int page = 1;
                URI uri = URI.create("https://api.themoviedb.org/3/search/movie?query=" + query + "&page=" + page + "&language=ko-KR");

                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));
                given(commonCodeService.convertCommonCodeNameToName("28", CodeGroupEnum.MOVIE_GENRE)).willReturn(convertedGenreCode);

                // when
                SearchMovieApiListResponse result = movieTMDbSearchService.searchMovie(query, page);

                // then
                assertThat(result).isNotNull();
                assertThat(result.results()).hasSize(1);
                assertThat(result.results().get(0).title()).isEqualTo("Inception");
                assertThat(result.results().get(0).genre_ids().get(0)).isEqualTo(convertedGenreCode);
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

    @Nested
    class SearchDetailsMovie {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void TMDb_영화_상세_조회() {
                // given
                String tmdbMovieId = "27205";
                SearchMovieDetailApiResponse response = mock(SearchMovieDetailApiResponse.class);

                ObjectMapper objectMapper = new ObjectMapper();
                String validJsonResponse = objectMapper.writeValueAsString(response);
                URI uri = URI.create("https://api.themoviedb.org/3/movie/" + tmdbMovieId + "?language=ko-KR");

                server.expect(MockRestRequestMatchers.requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));

                // when
                SearchMovieDetailApiResponse result = movieTMDbSearchService.searchDetailsMovie(tmdbMovieId);

                // then
                Assertions.assertThat(result).isNotNull();
            }
        }
    }

}
