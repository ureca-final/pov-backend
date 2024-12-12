//package net.pointofviews.curation.service;
//
//import net.pointofviews.curation.domain.Curation;
//import net.pointofviews.curation.domain.CurationCategory;
//import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
//import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
//import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
//import net.pointofviews.curation.repository.CurationRepository;
//import net.pointofviews.curation.service.impl.CurationMemberServiceImpl;
//import net.pointofviews.movie.repository.MovieRepository;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//@ExtendWith(MockitoExtension.class)
//class CurationMemberServiceTest {
//
//    @InjectMocks
//    private CurationMemberServiceImpl curationMemberService;
//
//    @Mock
//    private CurationRepository curationRepository;
//
//    @Mock
//    private CurationRedisService curationRedisService;
//
//    @Mock
//    private MovieRepository movieRepository;
//
//    @Nested
//    class ReadScheduledCurations {
//
//        @Test
//        void 큐레이션_스케줄_조회_성공() {
//            // given
//            Curation curation1 = Curation.builder()
//                    .theme("Theme1")
//                    .category(CurationCategory.GENRE)
//                    .title("Curation1")
//                    .description("Description1")
//                    .startTime(LocalDateTime.now())
//                    .build();
//            ReflectionTestUtils.setField(curation1, "id", 1L);
//
//            Curation curation2 = Curation.builder()
//                    .theme("Theme2")
//                    .category(CurationCategory.GENRE)
//                    .title("Curation2")
//                    .description("Description2")
//                    .startTime(LocalDateTime.now())
//                    .build();
//            ReflectionTestUtils.setField(curation2, "id", 2L);
//
//            List<Curation> mockCurations = List.of(curation1, curation2);
//            given(curationRepository.findAll()).willReturn(mockCurations);
//
//            Set<Long> movieIdsCuration1 = Set.of(1L, 2L);
//            Set<Long> movieIdsCuration2 = Set.of(3L, 4L);
//            given(curationRedisService.readMoviesForCuration(1L)).willReturn(movieIdsCuration1);
//            given(curationRedisService.readMoviesForCuration(2L)).willReturn(movieIdsCuration2);
//
//            List<ReadUserCurationMovieResponse> moviesForCuration1 = List.of(
//                    new ReadUserCurationMovieResponse("Movie1", "https://poster1.com", LocalDate.of(2023, 1, 1), 100L, 10L),
//                    new ReadUserCurationMovieResponse("Movie2", "https://poster2.com", LocalDate.of(2023, 2, 1), 200L, 20L)
//            );
//            List<ReadUserCurationMovieResponse> moviesForCuration2 = List.of(
//                    new ReadUserCurationMovieResponse("Movie3", "https://poster3.com", LocalDate.of(2023, 3, 1), 300L, 30L),
//                    new ReadUserCurationMovieResponse("Movie4", "https://poster4.com", LocalDate.of(2023, 4, 1), 400L, 40L)
//            );
//            given(movieRepository.findUserCurationMoviesByIds(movieIdsCuration1)).willReturn(moviesForCuration1);
//            given(movieRepository.findUserCurationMoviesByIds(movieIdsCuration2)).willReturn(moviesForCuration2);
//
//            // when
//            ReadUserCurationListResponse response = curationMemberService.readUserCurations();
//
//            // then
//            assertThat(response.userCurationList()).hasSize(2);
//
//            ReadUserCurationResponse curationResponse1 = response.userCurationList().get(0);
//            assertThat(curationResponse1.curationTitle()).isEqualTo("Curation1");
//            assertThat(curationResponse1.curationMovies()).hasSize(2);
//            assertThat(curationResponse1.curationMovies().get(0).title()).isEqualTo("Movie1");
//
//            ReadUserCurationResponse curationResponse2 = response.userCurationList().get(1);
//            assertThat(curationResponse2.curationTitle()).isEqualTo("Curation2");
//            assertThat(curationResponse2.curationMovies()).hasSize(2);
//            assertThat(curationResponse2.curationMovies().get(0).title()).isEqualTo("Movie3");
//
//            verify(curationRepository).findAll();
//            verify(curationRedisService).readMoviesForCuration(1L);
//            verify(curationRedisService).readMoviesForCuration(2L);
//            verify(movieRepository).findUserCurationMoviesByIds(movieIdsCuration1);
//            verify(movieRepository).findUserCurationMoviesByIds(movieIdsCuration2);
//        }
//
//        @Test
//        void 큐레이션_스케줄_조회_빈_결과() {
//            // given
//            given(curationRepository.findAll()).willReturn(Collections.emptyList());
//
//            // when
//            ReadUserCurationListResponse response = curationMemberService.readUserCurations();
//
//            // then
//            assertThat(response.userCurationList()).isEmpty();
//            verify(curationRepository).findAll();
//        }
//    }
//}