package net.pointofviews.movie.service;

import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.country.domain.Country;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.*;
import net.pointofviews.movie.dto.response.*;
import net.pointofviews.movie.repository.MovieContentRepository;
import net.pointofviews.movie.repository.MovieLikeCountRepository;
import net.pointofviews.movie.repository.MovieLikeRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.impl.MovieSearchServiceImpl;
import net.pointofviews.people.domain.People;
import net.pointofviews.review.domain.ReviewPreference;
import net.pointofviews.review.dto.ReviewDetailsWithLikeCountDto;
import net.pointofviews.review.dto.ReviewPreferenceCountDto;
import net.pointofviews.review.repository.ReviewRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MovieSearchServiceImplTest {
    @InjectMocks
    private MovieSearchServiceImpl movieSearchService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieLikeCountRepository movieLikeCountRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieContentRepository movieContentRepository;

    @Mock
    private MovieLikeRepository movieLikeRepository;

    @Mock
    private CommonCodeService commonCodeService;

    @Nested
    class SearchMovies {

        @Nested
        class Success {

            @Test
            void 영화_검색_결과_성공() {
                // given
                String query = "Inception";
                UUID memberId = UUID.randomUUID(); // UUID 생성
                Member loginMember = mock(Member.class);

                given(loginMember.getId()).willReturn(memberId);
                PageRequest pageable = PageRequest.of(0, 10);

                // Mocking 데이터 생성
                List<Object[]> mockResults = new ArrayList<>();
                mockResults.add(new Object[]{1L, "Inception", "https://example.com/poster.jpg", Date.valueOf("2010-07-16"), true, 123, 10});


                // Mocking된 Slice 객체 생성
                Slice<Object[]> mockSlice = new PageImpl<>(mockResults, pageable, mockResults.size());
                given(movieRepository.searchMoviesByTitleOrPeople(query, loginMember.getId(), pageable)).willReturn(mockSlice);

                // when
                SearchMovieListResponse response = movieSearchService.searchMovies(query, loginMember, pageable);

                // then
                assertThat(response.movies().getContent()).hasSize(1); // 결과가 1개 있어야 함
                SearchMovieResponse movie = response.movies().getContent().get(0);

                // 영화 정보 검증
                assertThat(movie.id()).isEqualTo(1L);
                assertThat(movie.title()).isEqualTo("Inception");
                assertThat(movie.poster()).isEqualTo("https://example.com/poster.jpg");
                assertThat(movie.movieLikeCount()).isEqualTo(123);
                assertThat(movie.movieReviewCount()).isEqualTo(10);
            }

            @Test
            void 영화_검색_결과없음() {
                // given
                String query = "NonExistentMovie";
                UUID memberId = UUID.randomUUID(); // UUID 생성
                Member loginMember = mock(Member.class);

                given(loginMember.getId()).willReturn(memberId);
                PageRequest pageable = PageRequest.of(0, 10);

                // 빈 결과를 반환하는 Slice 객체 Mocking
                Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
                given(movieRepository.searchMoviesByTitleOrPeople(query, loginMember.getId(), pageable)).willReturn(mockSlice);

                // when
                SearchMovieListResponse response = movieSearchService.searchMovies(query, loginMember, pageable);

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
                UUID memberId = UUID.randomUUID(); // UUID 생성
                Member loginMember = mock(Member.class);

                given(loginMember.getId()).willReturn(memberId);
                PageRequest pageable = PageRequest.of(0, 10);

                // when
                Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
                given(movieRepository.searchMoviesByTitleOrPeople(query, loginMember.getId(), pageable)).willReturn(mockSlice);

                SearchMovieListResponse response = movieSearchService.searchMovies(query, loginMember, pageable);

                // then
                assertThat(response.movies().getContent()).isEmpty(); // 비어 있는 결과
            }

            @Test
            void 영화_검색_시_DB_에러_발생() {
                // given
                String query = "Inception";
                UUID memberId = UUID.randomUUID(); // UUID 생성
                Member loginMember = mock(Member.class);

                given(loginMember.getId()).willReturn(memberId);
                PageRequest pageable = PageRequest.of(0, 10);

                // Mocking: DB 에러 발생
                given(movieRepository.searchMoviesByTitleOrPeople(query, loginMember.getId(), pageable))
                        .willThrow(new RuntimeException("Database Error"));

                // when & then
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    movieSearchService.searchMovies(query, loginMember, pageable);
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

    @Nested
    class ReadDetailMovie {

        @Nested
        class Success {

            @Test
            void 영화_상세_조회() {
                // given
                Long movieId = 1L;
                UUID memberId = UUID.randomUUID();
                MemberDetailsDto memberDetails = mock(MemberDetailsDto.class);
                Member member = mock(Member.class);

                given(memberDetails.member()).willReturn(member);
                given(member.getId()).willReturn(memberId);

                Movie mockMovie = movieFixture();
                mockMovie.addGenre(MovieGenre.builder().genreCode("01").movie(mockMovie).build());

                MovieCountry movieCountry = new MovieCountry(mock(Country.class));
                mockMovie.addCountry(movieCountry);

                MovieCrew mockCrew = mock(MovieCrew.class);
                mockMovie.addCrew(mockCrew);
                MovieCast mockCast = mock(MovieCast.class);
                mockMovie.addCast(mockCast);
                given(mockCrew.getPeople()).willReturn(People.builder().name("크리스토퍼 놀란").build());
                given(mockCast.getPeople()).willReturn(People.builder().name("레오나르도 디카프리오").build());

                List<ReviewPreferenceCountDto> mockReviewPreferences = List.of(
                        new ReviewPreferenceCountDto(10L, 2L)
                );

                List<MovieContent> mockMovieContents = List.of(
                        MovieContent.builder().content("https://image1.jpg").movie(mockMovie).contentType(MovieContentType.IMAGE).build(),
                        MovieContent.builder().content("https://video1.mp4").movie(mockMovie).contentType(MovieContentType.YOUTUBE).build()
                );

                List<ReviewDetailsWithLikeCountDto> mockTopReviews = List.of(
                        new ReviewDetailsWithLikeCountDto(
                                1L,
                                "인셉션 리뷰",
                                "이 영화는 상상력을 자극합니다.",
                                "https://example.com/thumbnail.jpg",
                                ReviewPreference.GOOD,
                                false,
                                false,
                                LocalDateTime.of(2024, 12, 13, 10, 0, 0),
                                20L,
                                "https://profile.image.com/user.jpg",
                                "사용자 닉네임",
                                true
                        )
                );

                given(movieRepository.findMovieWithDetailsById(movieId)).willReturn(Optional.of(mockMovie));
                given(commonCodeService.convertCommonCodeToName("01", CodeGroupEnum.MOVIE_GENRE)).willReturn("액션");
                given(movieLikeCountRepository.findById(movieId)).willReturn(Optional.of(MovieLikeCount.builder().movie(mockMovie).likeCount(5L).build()));
                given(reviewRepository.countReviewPreferenceByMovieId(movieId)).willReturn(mockReviewPreferences);
                given(movieContentRepository.findAllByMovieId(movieId)).willReturn(mockMovieContents);
                given(reviewRepository.findTop3ByMovieIdOrderByReviewLikeCountDesc(eq(movieId), eq(memberId), any(PageRequest.class)))
                        .willReturn(mockTopReviews);
                given(movieLikeRepository.existsByMovieIdAndMemberId(movieId, memberId)).willReturn(Boolean.TRUE);

                // when
                ReadDetailMovieResponse response = movieSearchService.readDetailMovie(movieId, memberDetails.member().getId());

                // then
                Assertions.assertThat(response.title()).isEqualTo(mockMovie.getTitle());
                Assertions.assertThat(response.released()).isEqualTo(mockMovie.getReleased());
                Assertions.assertThat(response.movieLikeCount()).isEqualTo(5L);
                Assertions.assertThat(response.genre()).containsExactly("액션");
                Assertions.assertThat(response.preferenceCounts()).hasSize(1);
                Assertions.assertThat(response.preferenceCounts().get(0).goodCount()).isEqualTo(10L);
                Assertions.assertThat(response.preferenceCounts().get(0).badCount()).isEqualTo(2L);
                Assertions.assertThat(response.images()).containsExactly("https://image1.jpg");
                Assertions.assertThat(response.videos()).containsExactly("https://video1.mp4");
                Assertions.assertThat(response.reviews()).hasSize(1);
                Assertions.assertThat(response.reviews().get(0).title()).isEqualTo("인셉션 리뷰");
                Assertions.assertThat(response.reviews().get(0).likeCount()).isEqualTo(20L);
            }
        }
    }

    private Movie movieFixture() {
        return Movie.builder()
                .title("인셉션")
                .plot("인셉션 스토리")
                .tmdbId(27205)
                .released(LocalDate.parse("2010-07-15"))
                .filmRating(KoreanFilmRating.TWELVE)
                .poster("인셉션 포스터")
                .backdrop("인셉션 배경")
                .build();
    }
}