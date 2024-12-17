package net.pointofviews.curation.service;

import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.exception.CurationMovieException;
import net.pointofviews.curation.service.impl.CurationRedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CurationRedisServiceImplTest {

    @InjectMocks
    private CurationRedisServiceImpl curationRedisService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Nested
    class SaveMoviesToCuration {

        @Test
        void 캐시에_영화_목록_저장_성공() {
            // given
            Long curationId = 1L;
            Set<Long> movieIds = Set.of(101L, 102L);
            String key = "curation:movies:1";

            given(redisTemplate.hasKey(key)).willReturn(false);

            ArgumentCaptor<Object[]> argumentCaptor = ArgumentCaptor.forClass(Object[].class);

            // when
            Set<Long> result = curationRedisService.saveMoviesToCuration(curationId, movieIds);

            // then
            assertThat(result).containsExactlyInAnyOrder(101L, 102L);
            verify(setOperations).add(eq(key), argumentCaptor.capture());

            // 검증: 순서에 상관없이 포함되어 있는지 확인
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder("101", "102");
        }

        @Nested
        class Failure {
            @Test
            void 이미_존재하는_데이터_CurationAlreadyExists_예외발생() {
                // given
                Long curationId = 1L;
                Set<Long> movieIds = Set.of(101L, 102L);
                String key = "curation:movies:1";

                given(redisTemplate.hasKey(key)).willReturn(true);

                // when & then
                assertThatThrownBy(() -> curationRedisService.saveMoviesToCuration(curationId, movieIds))
                        .isInstanceOf(CurationException.class);

                verify(setOperations, never()).add(any(), any());
            }
        }
    }

    @Nested
    class ReadMoviesForCuration {

        @Nested
        class Success {

            @Test
            void 캐시에서_영화_목록_조회_성공() {
                // given
                Long curationId = 1L;
                String key = "curation:movies:" + curationId;
                Set<Object> cachedMovies = Set.of("101", "102");

                given(redisTemplate.hasKey(key)).willReturn(true);
                given(setOperations.members(key)).willReturn(cachedMovies);

                // when
                Set<Long> result = curationRedisService.readMoviesForCuration(curationId);

                // then
                assertThat(result).containsExactlyInAnyOrder(101L, 102L);
            }
        }

        @Nested
        class Failure {

            @Test
            void 키가_존재하지_않으면_빈_결과반환() {
                // given
                Long curationId = 1L;
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(false);

                // when
                Set<Long> result = curationRedisService.readMoviesForCuration(curationId);

                // then
                assertThat(result).isEmpty();
            }
        }
    }

    @Nested
    class UpdateMoviesToCuration {

        @Nested
        class Success {
            @Test
            void 영화_목록_수정_성공() {
                // given
                Long curationId = 1L;
                Set<Long> newMovieIds = Set.of(201L, 202L);
                String key = "curation:movies:1";

                given(redisTemplate.hasKey(key)).willReturn(true);
                given(redisTemplate.delete(key)).willReturn(true);

                // when
                Set<Long> result = curationRedisService.updateMoviesToCuration(curationId, newMovieIds);

                // then
                assertThat(result).containsExactlyInAnyOrder(201L, 202L);
                verify(setOperations).add(eq(key), eq(201L), eq(202L));
            }
        }

        @Nested
        class Failure {
            @Test
            void 키가_존재하지_않으면_CurationMovieKeyNotFound_예외발생() {
                // given
                Long curationId = 1L;
                Set<Long> newMovieIds = Set.of(201L, 202L);
                String key = "curation:movies:1";

                given(redisTemplate.hasKey(key)).willReturn(false);

                // when & then
                assertThatThrownBy(() -> curationRedisService.updateMoviesToCuration(curationId, newMovieIds))
                        .isInstanceOf(CurationMovieException.class);

            }
        }
    }

    @Nested
    class DeleteAllMoviesForCuration {

        @Nested
        class Success {

            @Test
            void 캐시에서_모든_영화_삭제_성공() {
                // given
                Long curationId = 1L;
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(true);

                // when
                curationRedisService.deleteAllMoviesForCuration(curationId);

                // then
                verify(redisTemplate, times(1)).delete(key);
            }
        }

        @Nested
        class Failure {

            @Test
            void 키가_존재하지_않으면_CurationMovieKeyNotFound_예외발생() {
                // given
                Long curationId = 1L;
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(false);

                // when & then
                assertThrows(CurationMovieException.class, () -> curationRedisService.deleteAllMoviesForCuration(curationId));
            }
        }
    }


    @Nested
    class SaveTodayCurationId {

        @Test
        void 오늘의_큐레이션_ID_저장_성공() {
            // given
            Long curationId = 1L;
            String key = "curation:" + LocalDate.now();

            // when
            curationRedisService.saveTodayCurationId(curationId);

            // then
            verify(setOperations).add(eq(key), eq("1"));
        }
    }

    @Nested
    class ReadTodayCurationId {

        @Nested
        class Success {
            @Test
            void 오늘의_큐레이션_ID_조회_성공() {
                // given
                String key = "curation:" + LocalDate.now();
                given(setOperations.members(key)).willReturn(Set.of("1", "2"));

                // when
                Set<Long> result = curationRedisService.readTodayCurationId();

                // then
                assertThat(result).containsExactlyInAnyOrder(1L, 2L);
            }
        }

        @Nested
        class Failure {
            @Test
            void 오늘의_큐레이션_ID_조회_결과_없음() {
                // given
                String key = "curation:" + LocalDate.now();
                given(setOperations.members(key)).willReturn(null);

                // when
                Set<Long> result = curationRedisService.readTodayCurationId();

                // then
                assertThat(result).isEmpty();
            }
        }
    }
}