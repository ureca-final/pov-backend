package net.pointofviews.movie.service;

import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.impl.MovieServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @InjectMocks
    private MovieServiceImpl movieService;

    @Mock
    private MovieRepository movieRepository;

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
            void 존재하지_않는_영화로_삭제_시도(){
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
}
