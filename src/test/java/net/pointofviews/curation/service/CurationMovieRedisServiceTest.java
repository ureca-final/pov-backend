package net.pointofviews.curation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import net.pointofviews.curation.service.impl.CurationMovieRedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.exception.CurationMovieException;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CurationMovieRedisServiceTest {

    @InjectMocks
    private CurationMovieRedisServiceImpl curationMovieRedisService;

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
                Set<Long> savedMovies = curationMovieRedisService.saveMoviesToCuration(curationId, movieIds);

                // then
                assertThat(savedMovies).containsExactlyInAnyOrderElementsOf(movieIds);
                assertThat(savedMovies).containsExactlyInAnyOrder(101L, 102L, 103L);
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
                assertThrows(CurationException.class, () -> curationMovieRedisService.saveMoviesToCuration(curationId, movieIds));
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
                Set<Object> cachedMovies = Set.of(101L, 102L);

                given(redisTemplate.hasKey(key)).willReturn(true);
                given(redisTemplate.opsForSet().members(key)).willReturn(cachedMovies);

                // when
                Set<Long> result = curationMovieRedisService.readMoviesForCuration(curationId);

                // then
                assertThat(result).containsExactlyInAnyOrder(101L, 102L);
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
                assertThrows(CurationMovieException.class, () -> curationMovieRedisService.readMoviesForCuration(curationId));
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
                Set<Long> updatedMovies = curationMovieRedisService.updateMoviesToCuration(curationId, movieIds);

                // then
                assertThat(updatedMovies).containsExactlyInAnyOrderElementsOf(movieIds);
                verify(redisTemplate, times(1)).delete(key);
                assertThat(updatedMovies).containsExactlyInAnyOrder(201L, 202L);
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
                assertThrows(CurationMovieException.class, () -> curationMovieRedisService.updateMoviesToCuration(curationId, movieIds));
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
                curationMovieRedisService.deleteAllMoviesForCuration(curationId);

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
                assertThrows(CurationMovieException.class, () -> curationMovieRedisService.deleteAllMoviesForCuration(curationId));
            }
        }
    }
}