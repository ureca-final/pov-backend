package net.pointofviews.movie.service;

import net.pointofviews.common.service.RedisService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.exception.MovieLikeException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.impl.MovieMemberServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static net.pointofviews.movie.exception.MovieException.movieNotFound;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MovieMemberServiceImplTest {

    @InjectMocks
    private MovieMemberServiceImpl movieMemberService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RedisService redisService;

    @Nested
    class UpdateMovieLike {

        @Nested
        class Success {

            @Test
            void 영화_좋아요_성공() {
                // given
                long movieId = 1L;
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(movieRepository.existsById(movieId)).willReturn(true);
                given(loginMember.getId()).willReturn(memberId);
                given(redisService.getValue("MovieLiked:" + movieId + ":" + memberId))
                        .willReturn(null); // 아직 좋아요를 누르지 않은 상태

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() ->
                            movieMemberService.updateMovieLike(movieId, loginMember)
                    ).doesNotThrowAnyException();

                    verify(redisService).setValue(
                            eq("MovieLiked:" + movieId + ":" + memberId),
                            eq("true"),
                            any(Duration.class)
                    );
                    verify(redisService).setValue(
                            eq("MovieLikedCount:" + movieId),
                            any(),
                            any(Duration.class)
                    );
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
                // given
                long movieId = 1L;
                Member loginMember = mock(Member.class);

                given(movieRepository.existsById(movieId)).willReturn(false);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() ->
                                    movieMemberService.updateMovieLike(movieId, loginMember)
                            ).isInstanceOf(MovieException.class)
                            .hasMessage("영화(Id: 1)는 존재하지 않습니다.");
                });
            }

            @Test
            void 이미_좋아요한_영화_MovieLikeException_예외발생() {
                // given
                long movieId = 1L;
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(movieRepository.existsById(movieId)).willReturn(true);
                given(loginMember.getId()).willReturn(memberId);
                given(redisService.getValue("MovieLiked:" + movieId + ":" + memberId))
                        .willReturn("true"); // 이미 좋아요를 누른 상태

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() ->
                            movieMemberService.updateMovieLike(movieId, loginMember)
                    ).isInstanceOf(MovieLikeException.class);
                });
            }
        }
    }

    @Nested
    class UpdateMovieDislike {

        @Nested
        class Success {

            @Test
            void 영화_좋아요_취소_성공() {
                // given
                long movieId = 1L;
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(movieRepository.existsById(movieId)).willReturn(true);
                given(loginMember.getId()).willReturn(memberId);
                given(redisService.getValue("MovieLiked:" + movieId + ":" + memberId))
                        .willReturn("true"); // 이미 좋아요를 누른 상태

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatCode(() ->
                            movieMemberService.updateMovieDisLike(movieId, loginMember)
                    ).doesNotThrowAnyException();

                    verify(redisService).setValue(
                            eq("MovieLiked:" + movieId + ":" + memberId),
                            eq("false"),
                            any(Duration.class)
                    );
                    verify(redisService).setValue(
                            eq("MovieLikedCount:" + movieId),
                            any(),
                            any(Duration.class)
                    );
                });
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_영화_MovieNotFoundException_예외발생() {
                // given
                long movieId = 1L;
                Member loginMember = mock(Member.class);

                given(movieRepository.existsById(movieId)).willReturn(false);

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() ->
                                    movieMemberService.updateMovieDisLike(movieId, loginMember)
                            ).isInstanceOf(MovieException.class)
                            .hasMessage("영화(Id: 1)는 존재하지 않습니다.");
                });
            }

            @Test
            void 좋아요하지_않은_영화_MovieLikeException_예외발생() {
                // given
                long movieId = 1L;
                Member loginMember = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(movieRepository.existsById(movieId)).willReturn(true);
                given(loginMember.getId()).willReturn(memberId);
                given(redisService.getValue("MovieLiked:" + movieId + ":" + memberId))
                        .willReturn("false"); // 좋아요를 누르지 않은 상태

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() ->
                            movieMemberService.updateMovieDisLike(movieId, loginMember)
                    ).isInstanceOf(MovieLikeException.class);
                });
            }
        }
    }
}