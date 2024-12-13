package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMovieResponse;
import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;
import net.pointofviews.club.repository.ClubMoviesRepository;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.service.impl.ClubMovieServiceImpl;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.club.domain.Club;
import net.pointofviews.club.domain.ClubMovie;
import net.pointofviews.club.exception.ClubException;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.exception.MovieException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;



@ExtendWith(MockitoExtension.class)
class ClubMovieServiceImplTest {

    @InjectMocks
    private ClubMovieServiceImpl clubMovieService;

    @Mock
    private ClubMoviesRepository clubMoviesRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ClubRepository clubRepository;

    @Nested
    class ReadClubMovies {

        @Test
        void 클럽_영화_조회_성공() {
            // given
            UUID clubId = UUID.randomUUID();
            PageRequest pageable = PageRequest.of(0, 10);

            List<ReadClubMovieResponse> movieList = List.of(
                    new ReadClubMovieResponse("Inception", "https://example.com/poster1.jpg", LocalDate.of(2010, 7, 16), 156L, 15L),
                    new ReadClubMovieResponse("Interstellar", "https://example.com/poster2.jpg", LocalDate.of(2014, 11, 7), 200L, 25L)
            );
            Slice<ReadClubMovieResponse> movies = new SliceImpl<>(movieList, pageable, true);

            given(clubMoviesRepository.findMovieDetailsByClubId(clubId, pageable)).willReturn(movies);

            // when
            ReadClubMoviesListResponse response = clubMovieService.readClubMovies(clubId, pageable);

            // then
            assertThat(response.clubMovies().getContent()).hasSize(2);
            assertThat(response.clubMovies().getContent().get(0).title()).isEqualTo("Inception");
            assertThat(response.clubMovies().getContent().get(1).title()).isEqualTo("Interstellar");

            verify(clubMoviesRepository).findMovieDetailsByClubId(clubId, pageable);
        }

        @Test
        void 클럽_영화_조회_결과가_없는_경우() {
            // given
            UUID clubId = UUID.randomUUID();
            PageRequest pageable = PageRequest.of(0, 10);

            Slice<ReadClubMovieResponse> emptyMovies = new SliceImpl<>(List.of(), pageable, false);

            given(clubMoviesRepository.findMovieDetailsByClubId(clubId, pageable)).willReturn(emptyMovies);

            // when
            ReadClubMoviesListResponse response = clubMovieService.readClubMovies(clubId, pageable);

            // then
            assertThat(response.clubMovies().getContent()).isEmpty();

            verify(clubMoviesRepository).findMovieDetailsByClubId(clubId, pageable);
        }
    }


    @Nested
    class SaveMovieToMyClub {

        @Test
        void 영화_클럽에_추가_성공() {
            // given
            Long movieId = 1L;
            UUID clubId = UUID.randomUUID();

            Movie movie = mock(Movie.class);
            Club club = mock(Club.class);

            given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
            given(clubRepository.findById(clubId)).willReturn(Optional.of(club));
            given(clubMoviesRepository.existsByMovieIdAndClubId(movieId, clubId)).willReturn(false);

            // when
            clubMovieService.saveMovieToMyClub(movieId, clubId);

            // then
            verify(movieRepository).findById(movieId);
            verify(clubRepository).findById(clubId);
            verify(clubMoviesRepository).existsByMovieIdAndClubId(movieId, clubId);
            verify(clubMoviesRepository).save(any(ClubMovie.class));
        }

        @Test
        void 영화_클럽에_추가_실패_영화_존재하지_않음() {
            // given
            Long movieId = 1L;
            UUID clubId = UUID.randomUUID();

            given(movieRepository.findById(movieId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> clubMovieService.saveMovieToMyClub(movieId, clubId))
                    .isInstanceOf(MovieException.class)
                    .hasMessage(String.format("영화(Id: %d)는 존재하지 않습니다.", movieId));

            verify(movieRepository).findById(movieId);
            verify(clubRepository, never()).findById(clubId);
            verify(clubMoviesRepository, never()).existsByMovieIdAndClubId(movieId, clubId);
            verify(clubMoviesRepository, never()).save(any(ClubMovie.class));
        }

        @Test
        void 영화_클럽에_추가_실패_클럽_존재하지_않음() {
            // given
            Long movieId = 1L;
            UUID clubId = UUID.randomUUID();

            Movie movie = mock(Movie.class);
            given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
            given(clubRepository.findById(clubId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> clubMovieService.saveMovieToMyClub(movieId, clubId))
                    .isInstanceOf(ClubException.class)
                    .hasMessage(String.format("클럽(Id: %s)이 존재하지 않습니다.", clubId));

            verify(movieRepository).findById(movieId);
            verify(clubRepository).findById(clubId);
            verify(clubMoviesRepository, never()).existsByMovieIdAndClubId(movieId, clubId);
            verify(clubMoviesRepository, never()).save(any(ClubMovie.class));
        }

        @Test
        void 영화_클럽에_추가_실패_이미_추가된_영화() {
            // given
            Long movieId = 1L;
            UUID clubId = UUID.randomUUID();

            Movie movie = mock(Movie.class);
            Club club = mock(Club.class);

            given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
            given(clubRepository.findById(clubId)).willReturn(Optional.of(club));
            given(clubMoviesRepository.existsByMovieIdAndClubId(movieId, clubId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> clubMovieService.saveMovieToMyClub(movieId, clubId))
                    .isInstanceOf(MovieException.class)
                    .hasMessage(String.format("이미 클럽 북마크에 존재하는 영화(Id: %d) 입니다.", movieId));


            verify(movieRepository).findById(movieId);
            verify(clubRepository).findById(clubId);
            verify(clubMoviesRepository).existsByMovieIdAndClubId(movieId, clubId);
            verify(clubMoviesRepository, never()).save(any(ClubMovie.class));
        }
    }
}