package net.pointofviews.movie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.impl.CommonCodeServiceImpl;
import net.pointofviews.common.utils.LocaleUtils;
import net.pointofviews.movie.dto.response.*;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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
    private MockRestServiceServer mockServer;

    @Nested
    class SearchReleaseDate {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void 출시정보_검색_KR_존재() {
                // given
                String movieId = "12345";
                SearchReleaseApiResponse mockResponse = new SearchReleaseApiResponse(
                        12345,
                        List.of(
                                new SearchReleaseApiResponse.Result("KR", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("12", "ko", "2023-12-01", 3, null))),
                                new SearchReleaseApiResponse.Result("US", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("PG-13", "en", "2023-12-02", 3, null)))
                        )
                );

                mockApiResponse(movieId, mockResponse);

                // when
                SearchReleaseApiResponse response = movieTMDbSearchService.searchReleaseDate(movieId);

                // then
                assertThat(response.results()).hasSize(1);
                assertThat(response.results().get(0).iso_3166_1()).isEqualTo("KR");
                assertThat(response.results().get(0).release_dates()).hasSize(1);
                assertThat(response.results().get(0).release_dates().get(0).certification()).isEqualTo("12");
            }

            @Test
            @SneakyThrows
            void 출시정보_검색_KR_US_존재_여러결과() {
                // given
                String movieId = "12345";
                SearchReleaseApiResponse mockResponse = new SearchReleaseApiResponse(
                        12345,
                        List.of(
                                new SearchReleaseApiResponse.Result("US", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("PG-13", "en", "2023-12-02", 3, null))),
                                new SearchReleaseApiResponse.Result("KR", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("12", "ko", "2023-12-01", 3, null))),
                                new SearchReleaseApiResponse.Result("FR", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("Tous publics", "fr", "2023-12-03", 3, null)))
                        )
                );

                mockApiResponse(movieId, mockResponse);

                // when
                SearchReleaseApiResponse response = movieTMDbSearchService.searchReleaseDate(movieId);

                // then
                assertThat(response.results()).hasSize(1);
                assertThat(response.results().get(0).iso_3166_1()).isEqualTo("KR");
            }

            @Test
            @SneakyThrows
            void 출시정보_검색_KR_부재_US_존재() {
                // given
                String movieId = "12345";
                SearchReleaseApiResponse mockResponse = new SearchReleaseApiResponse(
                        12345,
                        List.of(
                                new SearchReleaseApiResponse.Result("US", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("PG-13", "en", "2023-12-02", 3, null)))
                        )
                );

                mockApiResponse(movieId, mockResponse);

                // when
                SearchReleaseApiResponse response = movieTMDbSearchService.searchReleaseDate(movieId);

                // then
                assertThat(response.results()).hasSize(1);
                assertThat(response.results().get(0).iso_3166_1()).isEqualTo("US");
                assertThat(response.results().get(0).release_dates()).hasSize(1);
                assertThat(response.results().get(0).release_dates().get(0).certification()).isEqualTo("PG-13");
            }

            @Test
            @SneakyThrows
            void 출시정보_검색_KR_US_부재() {
                // given
                String movieId = "12345";
                SearchReleaseApiResponse mockResponse = new SearchReleaseApiResponse(
                        12345,
                        List.of(
                                new SearchReleaseApiResponse.Result("FR", List.of(new SearchReleaseApiResponse.Result.ReleaseDate("Tous publics", "fr", "2023-12-03", 3, null)))
                        )
                );

                mockApiResponse(movieId, mockResponse);

                // when
                SearchReleaseApiResponse response = movieTMDbSearchService.searchReleaseDate(movieId);

                // then
                assertThat(response.results()).hasSize(1);
                assertThat(response.results().get(0).iso_3166_1()).isEqualTo("FR");
                assertThat(response.results().get(0).release_dates()).hasSize(1);
                assertThat(response.results().get(0).release_dates().get(0).certification()).isEqualTo("Tous publics");
            }

            @Test
            @SneakyThrows
            void 출시정보_검색_정보없음() {
                // given
                String movieId = "12345";
                SearchReleaseApiResponse mockResponse = new SearchReleaseApiResponse(12345, List.of());

                mockApiResponse(movieId, mockResponse);

                // when
                SearchReleaseApiResponse response = movieTMDbSearchService.searchReleaseDate(movieId);

                // then
                assertThat(response.results()).isEmpty();
            }
        }

        private void mockApiResponse(String movieId, SearchReleaseApiResponse mockResponse) throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            String validJsonResponse = objectMapper.writeValueAsString(mockResponse);
            URI uri = URI.create("https://api.themoviedb.org/3/movie/" + movieId + "/release_dates");

            mockServer.expect(requestTo(uri))
                    .andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class SearchLimit10Credit {

        @Nested
        class Success {

            @Test
            @SneakyThrows
            void 영화_크레딧_배우_최대_10명과_갑독_찾기() {
                // given
                String movieId = "27205";
                String koreanIsoCode = LocaleUtils.KOREAN_LANGUAGE_CODE;
                SearchCreditApiResponse mockResponse = createMockSearchCreditResponse();
                ObjectMapper objectMapper = new ObjectMapper();
                String validJsonResponse = objectMapper.writeValueAsString(mockResponse);
                URI uri = URI.create("https://api.themoviedb.org/3/movie/" + movieId + "/credits?language=" + koreanIsoCode);

                mockServer.expect(requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));

                // when
                SearchCreditApiResponse result = movieTMDbSearchService.searchLimit10Credit(movieId);

                // then
                assertThat(result.cast()).hasSize(10);
                assertThat(result.crew()).hasSize(1);
                assertThat(result.crew().get(0).job()).isEqualToIgnoringCase("director");
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

            return new SearchCreditApiResponse(castList, crewList);
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

                mockServer.expect(requestTo(uri)).andRespond(withSuccess(validJsonResponse, MediaType.APPLICATION_JSON));
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
                mockServer.expect(requestTo(uri)).andRespond(withBadRequest()
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
                mockServer.expect(requestTo(uri)).andRespond(withBadRequest()
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

                mockServer.expect(requestTo(uri)).andRespond(request -> mockResponse);

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
                String movieId = "27205";
                String koreanIsoCode = LocaleUtils.KOREAN_LANGUAGE_CODE;

                SearchMovieDetailApiResponse movieDetails = mock(SearchMovieDetailApiResponse.class);
                given(movieDetails.id()).willReturn(27205);
                given(movieDetails.title()).willReturn("Inception");
                given(movieDetails.overview()).willReturn("타인의 꿈에 들어가 생각을 훔치는 특수 보안요원 코브...");

                SearchCreditApiResponse movieCredits = mock(SearchCreditApiResponse.class);
                given(movieCredits.cast()).willReturn(new ArrayList<>());
                given(movieCredits.crew()).willReturn(new ArrayList<>());

                SearchReleaseApiResponse movieReleases = mock(SearchReleaseApiResponse.class);
                SearchReleaseApiResponse.Result releaseResult = mock(SearchReleaseApiResponse.Result.class);
                SearchReleaseApiResponse.Result.ReleaseDate releaseDate = mock(SearchReleaseApiResponse.Result.ReleaseDate.class);
                given(releaseDate.release_date()).willReturn("2010-07-15");
                given(releaseDate.certification()).willReturn("12");
                given(releaseResult.release_dates()).willReturn(List.of(releaseDate));
                given(movieReleases.results()).willReturn(List.of(releaseResult));

                mockServer.expect(requestTo("https://api.themoviedb.org/3/movie/" + movieId + "?language=" + koreanIsoCode))
                        .andRespond(withSuccess(new ObjectMapper().writeValueAsString(movieDetails), MediaType.APPLICATION_JSON));
                mockServer.expect(requestTo("https://api.themoviedb.org/3/movie/" + movieId + "/credits?language=" + koreanIsoCode))
                        .andRespond(withSuccess(new ObjectMapper().writeValueAsString(movieCredits), MediaType.APPLICATION_JSON));
                mockServer.expect(requestTo("https://api.themoviedb.org/3/movie/" + movieId + "/release_dates"))
                        .andRespond(withSuccess(new ObjectMapper().writeValueAsString(movieReleases), MediaType.APPLICATION_JSON));

                // when
                SearchFilteredMovieDetailResponse result = movieTMDbSearchService.searchDetailsMovie(movieId);

                // then
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.tmdbId()).isEqualTo(27205);
                Assertions.assertThat(result.title()).isEqualTo("Inception");
            }

            @Test
            @SneakyThrows
            void TMDb_영화_상세_조회_Releases_null() {
                // given
                String movieId = "27205";
                String koreanIsoCode = LocaleUtils.KOREAN_LANGUAGE_CODE;

                SearchMovieDetailApiResponse movieDetails = mock(SearchMovieDetailApiResponse.class);
                given(movieDetails.id()).willReturn(27205);
                given(movieDetails.title()).willReturn("Inception");
                given(movieDetails.overview()).willReturn("타인의 꿈에 들어가 생각을 훔치는 특수 보안요원 코브...");

                SearchCreditApiResponse movieCredits = mock(SearchCreditApiResponse.class);
                given(movieCredits.cast()).willReturn(new ArrayList<>());
                given(movieCredits.crew()).willReturn(new ArrayList<>());

                SearchReleaseApiResponse movieReleases = mock(SearchReleaseApiResponse.class);

                mockServer.expect(requestTo("https://api.themoviedb.org/3/movie/" + movieId + "?language=" + koreanIsoCode))
                        .andRespond(withSuccess(new ObjectMapper().writeValueAsString(movieDetails), MediaType.APPLICATION_JSON));
                mockServer.expect(requestTo("https://api.themoviedb.org/3/movie/" + movieId + "/credits?language=" + koreanIsoCode))
                        .andRespond(withSuccess(new ObjectMapper().writeValueAsString(movieCredits), MediaType.APPLICATION_JSON));
                mockServer.expect(requestTo("https://api.themoviedb.org/3/movie/" + movieId + "/release_dates"))
                        .andRespond(withSuccess(new ObjectMapper().writeValueAsString(movieReleases), MediaType.APPLICATION_JSON));

                // when
                SearchFilteredMovieDetailResponse result = movieTMDbSearchService.searchDetailsMovie(movieId);

                // then
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.tmdbId()).isEqualTo(27205);
                Assertions.assertThat(result.title()).isEqualTo("Inception");
            }
        }
    }

}
