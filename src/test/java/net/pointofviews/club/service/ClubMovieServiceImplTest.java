package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.ReadClubMovieResponse;
import net.pointofviews.club.dto.response.ReadClubMoviesListResponse;
import net.pointofviews.club.repository.ClubMoviesRepository;
import net.pointofviews.club.service.impl.ClubMovieServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubMovieServiceImplTest {

    @InjectMocks
    private ClubMovieServiceImpl clubMovieService;

    @Mock
    private ClubMoviesRepository clubMoviesRepository;

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
}