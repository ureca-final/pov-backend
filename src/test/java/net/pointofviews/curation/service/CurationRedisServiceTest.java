package net.pointofviews.curation.service;

import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.exception.CurationMovieException;
import net.pointofviews.curation.service.impl.CurationRedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CurationRedisServiceTest {

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

        @Nested
        class Success {

            @Test
            void 캐시에_영화_목록_저장_성공() {
                // given
                Long curationId = 1L;
                Set<Long> movieIds = Set.of(101L, 102L, 103L);
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(false);

                // when
                Set<Long> savedMovies = curationRedisService.saveMoviesToCuration(curationId, movieIds);

                // then
                assertThat(savedMovies).containsExactlyInAnyOrderElementsOf(movieIds);
                verify(redisTemplate, times(1)).opsForSet();
                verify(setOperations).add(eq(key), any());
            }
        }

        @Nested
        class Failure {

            @Test
            void 캐시_중복시_CurationAlreadyExists_예외발생() {
                // given
                Long curationId = 1L;
                Set<Long> movieIds = Set.of(101L, 102L, 103L);
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(true);

                // when & then
                assertThrows(CurationException.class, () -> curationRedisService.saveMoviesToCuration(curationId, movieIds));
                verify(redisTemplate, never()).opsForSet();
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
            void 캐시_영화_목록_수정_성공() {
                // given
                Long curationId = 1L;
                Set<Long> movieIds = Set.of(201L, 202L);
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(true);
                given(redisTemplate.delete(key)).willReturn(true);

                // when
                Set<Long> updatedMovies = curationRedisService.updateMoviesToCuration(curationId, movieIds);

                // then
                assertThat(updatedMovies).containsExactlyInAnyOrderElementsOf(movieIds);
                verify(redisTemplate, times(1)).delete(key);
                verify(setOperations).add(eq(key), any());
            }
        }

        @Nested
        class Failure {

            @Test
            void 키가_존재하지_않으면_CurationMovieKeyNotFound_예외발생() {
                // given
                Long curationId = 1L;
                Set<Long> movieIds = Set.of(201L, 202L);
                String key = "curation:movies:" + curationId;

                given(redisTemplate.hasKey(key)).willReturn(false);

                // when & then
                assertThrows(CurationMovieException.class, () -> curationRedisService.updateMoviesToCuration(curationId, movieIds));
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
}