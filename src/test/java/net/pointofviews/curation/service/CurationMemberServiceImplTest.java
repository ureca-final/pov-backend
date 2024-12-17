package net.pointofviews.curation.service;

import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.impl.CurationMemberServiceImpl;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.repository.MovieRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CurationMemberServiceImplTest {

    @InjectMocks
    private CurationMemberServiceImpl curationMemberService;

    @Mock
    private CurationRedisService curationRedisService;

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private MovieRepository movieRepository;

    @Nested
    class ReadUserCurations {

        @Test
        void 오늘_큐레이션_조회_성공() {
            // given
            UUID memberId = UUID.randomUUID(); // UUID 생성
            Member loginMember = mock(Member.class);

            given(loginMember.getId()).willReturn(memberId);

            // Redis에서 큐레이션 ID 조회
            Set<Long> curationIds = Set.of(1L, 2L);
            given(curationRedisService.readTodayCurationId()).willReturn(curationIds);

            // 각 큐레이션의 title 조회
            given(curationRepository.findTitleById(1L)).willReturn("Curation 1");
            given(curationRepository.findTitleById(2L)).willReturn("Curation 2");

            // Redis에서 큐레이션 별 영화 ID 조회
            Set<Long> movieIds1 = Set.of(101L, 102L);
            Set<Long> movieIds2 = Set.of(103L, 104L);
            given(curationRedisService.readMoviesForCuration(1L)).willReturn(movieIds1);
            given(curationRedisService.readMoviesForCuration(2L)).willReturn(movieIds2);

            // 영화 정보 DB 조회
            List<ReadUserCurationMovieResponse> movieDetails1 = List.of(
                    new ReadUserCurationMovieResponse("Movie 1", "Poster 1", LocalDate.of(2023, 1, 1), true, 10L, 5L),
                    new ReadUserCurationMovieResponse("Movie 2", "Poster 2", LocalDate.of(2023, 2, 1), false, 20L, 10L)
            );

            List<ReadUserCurationMovieResponse> movieDetails2 = List.of(
                    new ReadUserCurationMovieResponse("Movie 3", "Poster 3", LocalDate.of(2023, 3, 1), true, 30L, 15L),
                    new ReadUserCurationMovieResponse("Movie 4", "Poster 4", LocalDate.of(2023, 4, 1), false, 40L, 20L)
            );

            given(movieRepository.findUserCurationMoviesByIds(movieIds1, memberId)).willReturn(movieDetails1);
            given(movieRepository.findUserCurationMoviesByIds(movieIds2, memberId)).willReturn(movieDetails2);

            // when
            ReadUserCurationListResponse response = curationMemberService.readUserCurations(loginMember);

            // then
            assertThat(response.userCurationList()).hasSize(2);

            // 첫 번째 큐레이션 검증
            ReadUserCurationResponse curation1 = response.userCurationList().get(0);
            assertThat(curation1.curationTitle()).isEqualTo("Curation 1");
            assertThat(curation1.curationMovies()).hasSize(2);

            // 두 번째 큐레이션 검증
            ReadUserCurationResponse curation2 = response.userCurationList().get(1);
            assertThat(curation2.curationTitle()).isEqualTo("Curation 2");
            assertThat(curation2.curationMovies()).hasSize(2);

            verify(curationRedisService).readTodayCurationId();
            verify(curationRepository).findTitleById(1L);
            verify(curationRepository).findTitleById(2L);
            verify(movieRepository).findUserCurationMoviesByIds(movieIds1, memberId);
            verify(movieRepository).findUserCurationMoviesByIds(movieIds2, memberId);
        }

        @Test
        void 오늘_큐레이션_없음() {
            // given
            Member loginMember = mock(Member.class);

            given(curationRedisService.readTodayCurationId()).willReturn(Set.of());

            // when
            ReadUserCurationListResponse response = curationMemberService.readUserCurations(loginMember);

            // then
            assertThat(response.userCurationList()).isEmpty();

            verify(curationRedisService).readTodayCurationId();
            verifyNoInteractions(curationRepository, movieRepository);
        }
    }
}