package net.pointofviews.movie.service;

import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import net.pointofviews.movie.dto.response.SearchMovieResponse;
import net.pointofviews.movie.service.impl.MovieSearchServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import net.pointofviews.movie.dto.response.AdminSearchMovieListResponse;
import net.pointofviews.movie.dto.response.AdminSearchMovieResponse;
import net.pointofviews.movie.repository.MovieRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MovieSearchServiceImplTest {
    @InjectMocks
    private MovieSearchServiceImpl movieSearchService;

    @Mock
    private MovieRepository movieRepository;

    @Nested
    class SearchMovies {

        @Nested
        class Success {

            @Test
            void 영화_검색_결과_성공() {
                // given
                String query = "Inception";
                PageRequest pageable = PageRequest.of(0, 10);

                // Mocking 데이터 생성
                List<Object[]> mockResults = new ArrayList<>();
                mockResults.add(new Object[]{1L, "Inception", "https://example.com/poster.jpg", Date.valueOf("2010-07-16"), 123, 10});


                // Mocking된 Slice 객체 생성
                Slice<Object[]> mockSlice = new PageImpl<>(mockResults, pageable, mockResults.size());
                given(movieRepository.searchMoviesByTitleOrPeople(query, pageable)).willReturn(mockSlice);

                // when
                SearchMovieListResponse response = movieSearchService.searchMovies(query, pageable);

                // then
                assertThat(response.movies().getContent()).hasSize(1); // 결과가 1개 있어야 함
                SearchMovieResponse movie = response.movies().getContent().get(0);

                // 영화 정보 검증
                assertThat(movie.id()).isEqualTo(1L);
                assertThat(movie.title()).isEqualTo("Inception");
                assertThat(movie.poster()).isEqualTo("https://example.com/poster.jpg");
                assertThat(movie.movieLikeCount()).isEqualTo(123);
                assertThat(movie.reviewCount()).isEqualTo(10);
            }

            @Test
            void 영화_검색_결과없음() {
                // given
                String query = "NonExistentMovie";
                PageRequest pageable = PageRequest.of(0, 10);

                // 빈 결과를 반환하는 Slice 객체 Mocking
                Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
                given(movieRepository.searchMoviesByTitleOrPeople(query, pageable)).willReturn(mockSlice);

                // when
                SearchMovieListResponse response = movieSearchService.searchMovies(query, pageable);

                // then
                assertThat(response.movies().getContent()).isEmpty(); // 결과가 없어야 함
            }
        }

        @Nested
        class Failure {

            @Test
            void 영화_검색_시_쿼리가_없으면_실패() {
                // given
                String query = null; // 잘못된 입력값
                PageRequest pageable = PageRequest.of(0, 10);

                // when
                Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
                given(movieRepository.searchMoviesByTitleOrPeople(query, pageable)).willReturn(mockSlice);

                SearchMovieListResponse response = movieSearchService.searchMovies(query, pageable);

                // then
                assertThat(response.movies().getContent()).isEmpty(); // 비어 있는 결과
            }

            @Test
            void 영화_검색_시_DB_에러_발생() {
                // given
                String query = "Inception";
                PageRequest pageable = PageRequest.of(0, 10);

                // Mocking: DB 에러 발생
                given(movieRepository.searchMoviesByTitleOrPeople(query, pageable))
                        .willThrow(new RuntimeException("Database Error"));

                // when & then
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    movieSearchService.searchMovies(query, pageable);
                });
            }
        }
    }


    @Nested
    class AdminSearchMovies {

        @Nested
        class Success {

            @Test
            void 관리자_영화_검색_성공() {
                // given
                String query = "Inception";
                Pageable pageable = PageRequest.of(0, 10);

                // Mocking 데이터 생성
                List<Object[]> mockResults = new ArrayList<>();
                mockResults.add(new Object[]{1L, "Inception", Date.valueOf("2010-07-16")});

                Slice<Object[]> mockSlice = new PageImpl<>(mockResults, pageable, mockResults.size());

                // Repository Mocking
                given(movieRepository.adminSearchMovies(query, pageable)).willReturn(mockSlice);

                // when
                AdminSearchMovieListResponse response = movieSearchService.adminSearchMovies(query, pageable);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.curationMovies().getContent()).hasSize(1);

                    AdminSearchMovieResponse movie = response.curationMovies().getContent().get(0);
                    softly.assertThat(movie.id()).isEqualTo(1L);
                    softly.assertThat(movie.title()).isEqualTo("Inception");
                    softly.assertThat(movie.released()).isEqualTo(LocalDate.of(2010, 7, 16));
                });
            }

            @Test
            void 관리자_영화_검색_결과없음() {
                // given
                String query = "NonExistentMovie";
                PageRequest pageable = PageRequest.of(0, 10);

                // 빈 결과를 반환하는 Slice Mocking
                Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
                given(movieRepository.adminSearchMovies(query, pageable)).willReturn(mockSlice);

                // when
                AdminSearchMovieListResponse response = movieSearchService.adminSearchMovies(query, pageable);

                // then
                assertThat(response.curationMovies().getContent()).isEmpty(); // 결과가 없어야 함
            }
        }

        @Nested
        class Failure {

            @Test
            void 관리자_영화_검색_쿼리_없음_실패() {
                // given
                String query = null; // 잘못된 입력값
                PageRequest pageable = PageRequest.of(0, 10);

                // 빈 Slice 객체를 반환하도록 Mocking
                Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
                given(movieRepository.adminSearchMovies(query, pageable)).willReturn(mockSlice);

                // when
                AdminSearchMovieListResponse response = movieSearchService.adminSearchMovies(query, pageable);

                // then
                assertThat(response.curationMovies().getContent()).isEmpty(); // 결과가 없어야 함
            }

            @Test
            void 관리자_영화_검색_DB_에러_발생() {
                // given
                String query = "Inception";
                PageRequest pageable = PageRequest.of(0, 10);

                // Mocking: DB 에러 발생
                given(movieRepository.adminSearchMovies(query, pageable))
                        .willThrow(new RuntimeException("Database Error"));

                // when & then
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    movieSearchService.adminSearchMovies(query, pageable);
                });
            }
        }
    }
}