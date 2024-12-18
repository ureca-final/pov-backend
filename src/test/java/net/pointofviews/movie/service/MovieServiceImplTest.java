package net.pointofviews.movie.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.country.domain.Country;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.*;
import net.pointofviews.movie.dto.request.PutMovieRequest;
import net.pointofviews.movie.dto.request.PutMovieRequest.UpdateMoviePeopleRequest;
import net.pointofviews.movie.dto.response.MovieListResponse;
import net.pointofviews.movie.dto.response.MovieResponse;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieResponse;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieContentRepository;
import net.pointofviews.movie.repository.MovieLikeCountRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.impl.MovieCountryServiceImpl;
import net.pointofviews.movie.service.impl.MoviePeopleServiceImpl;
import net.pointofviews.movie.service.impl.MovieServiceImpl;
import net.pointofviews.people.domain.People;
import net.pointofviews.review.domain.Review;
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
import org.springframework.data.domain.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @InjectMocks
    private MovieServiceImpl movieService;

    @Mock
    private CommonCodeService commonCodeService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieCountryServiceImpl movieCountryServiceImpl;

    @Mock
    private MoviePeopleServiceImpl moviePeopleServiceImpl;

    @Nested
    class DeleteMovie {

        @Nested
        class Success {

            @Test
            void 영화_삭제() {
                // given
                Long movieId = 1L;

                given(movieRepository.existsById(1L)).willReturn(true);

                // when
                movieService.deleteMovie(movieId);

                // then
                verify(movieRepository, times(1)).deleteById(anyLong());
            }
        }

        @Nested
        class failure {

            @Test
            void 존재하지_않는_영화로_삭제_시도() {
                // given
                Long wrongId = 0L;

                given(movieRepository.existsById(wrongId)).willReturn(false);

                // when & then
                Assertions.assertThatThrownBy(
                        () -> movieService.deleteMovie(wrongId)
                ).isInstanceOf(MovieException.class).hasMessage(MovieException.movieNotFound(wrongId).getMessage());

                verify(movieRepository, never()).deleteById(anyLong());
            }
        }
    }

    @Nested
    class UpdateMovie {

        @Nested
        class Success {

            @Test
            void 영화_수정() {
                // given
                PutMovieRequest request = putRequestFixture();

                Movie mockMovie = spy(movieFixture());
                Long movieId = 1L;

                given(movieRepository.findById(movieId)).willReturn(Optional.of(mockMovie));
                given(commonCodeService.convertCommonCodeDescriptionToCode("액션", CodeGroupEnum.MOVIE_GENRE)).willReturn("01");
                given(commonCodeService.convertCommonCodeDescriptionToCode("SF", CodeGroupEnum.MOVIE_GENRE)).willReturn("15");

                given(movieCountryServiceImpl.saveMovieCountries(any(Country.class))).willReturn(mock(Country.class));
                given(moviePeopleServiceImpl.savePeopleIfNotExists(any(People.class))).willReturn(mock(People.class));

                // when
                movieService.updateMovie(movieId, request);

                // then
                verify(movieRepository).findById(movieId);
                verify(mockMovie).updateMovie(
                        eq(request),
                        anyList(),
                        anyList(),
                        anyList(),
                        anyList()
                );

            }
        }
    }


    @Nested
    class ReadMovies {
        @Test
        void 영화_리스트_조회_성공() {
            // given
            UUID memberId = UUID.randomUUID();
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(memberId);

            PageRequest pageable = PageRequest.of(0, 10);

            // Mocking 데이터 생성
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{"Inception", "https://example.com/poster.jpg", LocalDate.now(), true, 123, 10});


            Slice<Object[]> mockSlice = new PageImpl<>(mockResults, pageable, mockResults.size());
            given(movieRepository.findAllMovies(loginMember.getId(), pageable)).willReturn(mockSlice);


            // when
            MovieListResponse response = movieService.readMovies(memberId, pageable);

            // then
            Assertions.assertThat(response.movies().getContent()).hasSize(1); // 결과가 1개 있어야 함

            MovieResponse movie = response.movies().getContent().get(0);

            // 영화 정보 검증
            Assertions.assertThat(movie.title()).isEqualTo("Inception");
            Assertions.assertThat(movie.poster()).isEqualTo("https://example.com/poster.jpg");
            Assertions.assertThat(movie.movieLikeCount()).isEqualTo(123);
            Assertions.assertThat(movie.movieReviewCount()).isEqualTo(10);
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

    private PutMovieRequest putRequestFixture() {
        UpdateMoviePeopleRequest.CastRequest cast1 = new UpdateMoviePeopleRequest.CastRequest(
                2L,
                6193,
                "Leonardo DiCaprio",
                "/wo2hJpn04vbtmh0B9utCFdsQhxM.jpg",
                "Dom Cobb",
                0
        );

        UpdateMoviePeopleRequest.CastRequest cast2 = new UpdateMoviePeopleRequest.CastRequest(
                3L,
                1234,
                "Joseph Gordon-Levitt",
                "/profilePath.jpg",
                "Arthur",
                1
        );

        UpdateMoviePeopleRequest.CrewRequest crew1 = new UpdateMoviePeopleRequest.CrewRequest(
                1L,
                525,
                "Christopher Nolan",
                "Christopher Nolan",
                "/cGOPbv9wA5gEejkUN892JrveARt.jpg",
                "Directing",
                "Director"
        );

        UpdateMoviePeopleRequest.CrewRequest crew2 = new UpdateMoviePeopleRequest.CrewRequest(
                4L,
                1235,
                "Hans Zimmer",
                "Hans Zimmer",
                "/profilePath2.jpg",
                "Music",
                "Composer"
        );

        UpdateMoviePeopleRequest updateMoviePeopleRequest = new UpdateMoviePeopleRequest(
                List.of(cast1, cast2),
                List.of(crew1, crew2)
        );

        return new PutMovieRequest(
                "인셉션",
                List.of("액션", "SF"),
                List.of("미국"),
                LocalDate.of(2010, 7, 16),
                "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                updateMoviePeopleRequest
        );
    }
}
