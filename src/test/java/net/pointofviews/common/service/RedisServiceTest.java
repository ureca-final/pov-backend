package net.pointofviews.common.service;

import net.pointofviews.common.exception.RedisException;
import net.pointofviews.common.repository.RedisRepository;
import net.pointofviews.common.service.impl.StringRedisServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @InjectMocks
    private StringRedisServiceImpl redisService;

    @Mock
    private RedisRepository redisRepository;

    private static final String TEST_KEY = "testKey";
    private static final String TEST_VALUE = "testValue";
    private static final Duration TEST_TTL = Duration.ofMinutes(5);

    @Nested
    class GetValue {

        @Nested
        class Success {

            @Test
            void 값을_성공적으로_가져옴() {
                // given
                given(redisRepository.getValue(TEST_KEY)).willReturn(TEST_VALUE);

                // when
                String value = redisService.getValue(TEST_KEY);

                // then
                assertThat(value).isEqualTo(TEST_VALUE);
                then(redisRepository).should().getValue(TEST_KEY);
            }
        }

        @Nested
        class Failure {

            @Test
            void 값을_가져오는_도중_예외가_발생하면_RedisException_던짐() {
                // given
                willThrow(new RuntimeException("Redis error")).given(redisRepository).getValue(TEST_KEY);

                // when & then
                assertThatThrownBy(() -> redisService.getValue(TEST_KEY))
                        .isInstanceOf(RedisException.class)
                        .hasMessage(RedisException.redisServerError(TEST_KEY).getMessage());
                then(redisRepository).should().getValue(TEST_KEY);
            }
        }
    }

    @Nested
    class SetValue {

        @Nested
        class Success {

            @Test
            void 값을_TTL과_함께_성공적으로_저장() {
                // when
                redisService.setValue(TEST_KEY, TEST_VALUE, TEST_TTL);

                // then
                then(redisRepository).should().setValueWithTTL(TEST_KEY, TEST_VALUE, TEST_TTL);
            }
        }

        @Nested
        class Failure {

            @Test
            void 값을_저장하는_도중_예외가_발생하면_RedisException_던짐() {
                // given
                willThrow(new RuntimeException("Redis error")).given(redisRepository).setValueWithTTL(TEST_KEY, TEST_VALUE, TEST_TTL);

                // when & then
                assertThatThrownBy(() -> redisService.setValue(TEST_KEY, TEST_VALUE, TEST_TTL))
                        .isInstanceOf(RedisException.class)
                        .hasMessage(RedisException.redisServerError(TEST_KEY).getMessage());
                then(redisRepository).should().setValueWithTTL(TEST_KEY, TEST_VALUE, TEST_TTL);
            }
        }
    }

    @Nested
    class AddToSet {

        @Nested
        class Success {

            @Test
            void 값을_Set에_성공적으로_추가() {
                // given
                given(redisRepository.addToSet(TEST_KEY, TEST_VALUE)).willReturn(1L);

                // when
                Long result = redisService.addToSet(TEST_KEY, TEST_VALUE);

                // then
                assertThat(result).isEqualTo(1L);
                then(redisRepository).should().addToSet(TEST_KEY, TEST_VALUE);
            }
        }

        @Nested
        class Failure {

            @Test
            void Set에_추가하는_도중_예외가_발생하면_RedisException_던짐() {
                // given
                willThrow(new RuntimeException("Redis error")).given(redisRepository).addToSet(TEST_KEY, TEST_VALUE);

                // when & then
                assertThatThrownBy(() -> redisService.addToSet(TEST_KEY, TEST_VALUE))
                        .isInstanceOf(RedisException.class)
                        .hasMessage(RedisException.redisServerError(TEST_KEY).getMessage());
                then(redisRepository).should().addToSet(TEST_KEY, TEST_VALUE);
            }

            @Test
            void Set에_추가결과가_null이면_예외_발생() {
                // given
                given(redisRepository.addToSet(TEST_KEY, TEST_VALUE)).willReturn(null);

                // when & then
                assertThatThrownBy(() -> redisService.addToSet(TEST_KEY, TEST_VALUE))
                        .isInstanceOf(RedisException.class)
                        .hasMessage(RedisException.redisServerError(TEST_KEY).getMessage());
                then(redisRepository).should().addToSet(TEST_KEY, TEST_VALUE);
            }
        }
    }
}
