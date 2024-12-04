package net.pointofviews.curation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationMovieRedisService;
import net.pointofviews.curation.service.impl.CurationServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CurationServiceTest {

    @InjectMocks
    private CurationServiceImpl curationService;

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private CurationMovieRedisService curationMovieRedisService;

    @Nested
    class ReadAllCurations {

        @Nested
        class Success {

            @Test
            void 모든_큐레이션_조회_성공() throws Exception {
                // given
                Curation curation1 = Curation.builder()
                        .theme("Action Movies")
                        .category(CurationCategory.GENRE)
                        .title("Top Action")
                        .description("Best action movies of 2024")
                        .startTime(LocalDateTime.of(2024, 11, 28, 10, 0))
                        .build();

                Curation curation2 = Curation.builder()
                        .theme("Romantic Comedies")
                        .category(CurationCategory.GENRE)
                        .title("Love and Laughter")
                        .description("Feel-good romantic comedies")
                        .startTime(LocalDateTime.of(2024, 12, 1, 15, 30))
                        .build();

                // 리플렉션을 사용하여 id 설정
                setCurationId(curation1, 1L);
                setCurationId(curation2, 2L);

                List<Curation> curations = List.of(curation1, curation2);
                given(curationRepository.findAll()).willReturn(curations);

                // when
                ReadCurationListResponse response = curationService.readAllCurations();

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.curations()).hasSize(2);

                    softly.assertThat(response.curations().get(0).id()).isEqualTo(1L);
                    softly.assertThat(response.curations().get(0).theme()).isEqualTo("Action Movies");
                    softly.assertThat(response.curations().get(0).category()).isEqualTo(CurationCategory.GENRE);
                    softly.assertThat(response.curations().get(0).title()).isEqualTo("Top Action");
                    softly.assertThat(response.curations().get(0).description()).isEqualTo("Best action movies of 2024");

                    softly.assertThat(response.curations().get(1).id()).isEqualTo(2L);
                    softly.assertThat(response.curations().get(1).theme()).isEqualTo("Romantic Comedies");
                    softly.assertThat(response.curations().get(1).category()).isEqualTo(CurationCategory.GENRE);
                    softly.assertThat(response.curations().get(1).title()).isEqualTo("Love and Laughter");
                    softly.assertThat(response.curations().get(1).description()).isEqualTo("Feel-good romantic comedies");
                });

                verify(curationRepository, times(1)).findAll();
            }
        }

        @Nested
        class Failure {

            @Test
            void 큐레이션이_없으면_빈_목록_반환() {
                // given
                given(curationRepository.findAll()).willReturn(List.of());

                // when
                ReadCurationListResponse response = curationService.readAllCurations();

                // then
                assertThat(response.curations()).isEmpty();
                verify(curationRepository, times(1)).findAll();
            }
        }
    }

    // 리플렉션으로 id 필드 설정
    private void setCurationId(Curation curation, Long id) throws Exception {
        Field idField = Curation.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(curation, id);
    }

    @Nested
    class ReadCuration {

        @Nested
        class Success {

            @Test
            void 특정_큐레이션_조회_성공() throws Exception {
                // given
                Long curationId = 1L;

                Curation curation = Curation.builder()
                        .theme("Action Movies")
                        .category(CurationCategory.GENRE)
                        .title("Top Action")
                        .description("Best action movies of 2024")
                        .startTime(LocalDateTime.of(2024, 11, 28, 10, 0))
                        .build();

                // 리플렉션으로 id 설정
                setCurationId(curation, curationId);

                Set<Long> cachedMovieIds = Set.of(101L, 102L, 103L);

                given(curationRepository.findById(curationId)).willReturn(Optional.of(curation));
                given(curationMovieRedisService.readMoviesForCuration(curationId)).willReturn(cachedMovieIds);

                // when
                ReadCurationMoviesResponse response = curationService.readCuration(curationId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.readCurationResponse().id()).isEqualTo(curationId);
                    softly.assertThat(response.readCurationResponse().theme()).isEqualTo("Action Movies");
                    softly.assertThat(response.readCurationResponse().category()).isEqualTo(CurationCategory.GENRE);
                    softly.assertThat(response.readCurationResponse().title()).isEqualTo("Top Action");
                    softly.assertThat(response.readCurationResponse().description()).isEqualTo("Best action movies of 2024");
                    softly.assertThat(response.readCurationResponse().startTime()).isEqualTo(LocalDateTime.of(2024, 11, 28, 10, 0));

                    softly.assertThat(response.movieIds()).containsExactlyInAnyOrder(101L, 102L, 103L);
                });

                verify(curationRepository, times(1)).findById(curationId);
                verify(curationMovieRedisService, times(1)).readMoviesForCuration(curationId);
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_큐레이션_조회시_CurationNotFound_예외발생() {
                // given
                Long curationId = 1L;

                given(curationRepository.findById(curationId)).willReturn(Optional.empty());

                // when & then
                assertThrows(CurationException.class, () -> curationService.readCuration(curationId));

                verify(curationRepository, times(1)).findById(curationId);
                verify(curationMovieRedisService, never()).readMoviesForCuration(anyLong());
            }
        }
    }

}