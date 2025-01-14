package net.pointofviews.movie.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import net.pointofviews.movie.domain.KoreanFilmRating;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.people.domain.People;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "영화 등록 요청 DTO")
public record CreateMovieRequest(
        @Schema(description = "TMDB 영화 ID", example = "27205")
        @NotNull
        Integer tmdbId,

        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "영화 줄거리", example = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life...")
        String plot,

        @Schema(description = "영화 포스터 경로", example = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg")
        String poster,

        @Schema(description = "영화 배경 이미지 경로", example = "/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg")
        String backdrop,

        @ArraySchema(arraySchema = @Schema(description = "제작 국가 목록", example = "[\"미국\", \"영국\"]"))
        List<String> originCountries,

        @Schema(description = "영화 개봉일", example = "2010-07-15")
        String released,

        @Schema(description = "영화 심의 등급", example = "12")
        String filmRating,

        @ArraySchema(arraySchema = @Schema(description = "영화 장르 목록", example = "[\"액션\", \"SF\", \"모험\"]"))
        List<String> genres,

        @Schema(description = "영화 출연진 및 제작진 정보")
        SearchCreditApiRequest peoples
) {
    public Movie toMovieEntity() {
        return Movie.builder()
                .title(title)
                .backdrop(backdrop)
                .poster(poster)
                .plot(plot)
                .filmRating(KoreanFilmRating.of(filmRating))
                .released(LocalDate.parse(released))
                .tmdbId(tmdbId)
                .build();
    }

    @Schema(description = "TMDB 영화 출연진 및 제작진 정보 요청 DTO")
    public record SearchCreditApiRequest(
            @Schema(description = "출연진 목록")
            List<CastRequest> cast,

            @Schema(description = "제작진 목록")
            List<CrewRequest> crew
    ) {
        @Schema(description = "출연진 정보 요청")
        public record CastRequest(
                @Schema(description = "성별 (1: 여성, 2: 남성, null: 알 수 없음)", example = "2")
                Integer gender,

                @Schema(description = "출연진 ID", example = "6193")
                @NotNull
                int id,

                @Schema(description = "출연진 이름", example = "Leonardo DiCaprio")
                String name,

                @Schema(description = "출연진 프로필 이미지 경로", example = "/wo2hJpn04vbtmh0B9utCFdsQhxM.jpg")
                String profile_path,

                @Schema(description = "출연진 배역 이름", example = "Dom Cobb")
                String character,

                @Schema(description = "출연진 순서", example = "0")
                int order
        ) {
            public MovieCast toMovieCastEntity() {
                return MovieCast.builder()
                        .roleName(character)
                        .displayOrder(order)
                        .build();
            }

            public People toPeopleEntity() {
                return People.builder()
                        .imageUrl(profile_path)
                        .name(name)
                        .tmdbId(id)
                        .build();
            }
        }

        @Schema(description = "제작진 정보 요청")
        public record CrewRequest(
                @Schema(description = "성별 (1: 여성, 2: 남성, null: 알 수 없음)", example = "2")
                Integer gender,

                @Schema(description = "제작진 ID", example = "525")
                @NotNull
                int id,

                @Schema(description = "제작진 이름", example = "Christopher Nolan")
                String name,

                @Schema(description = "제작진 원어 이름", example = "Christopher Nolan")
                String original_name,

                @Schema(description = "인기 점수", example = "87.0")
                double popularity,

                @Schema(description = "제작진 프로필 이미지 경로", example = "/cGOPbv9wA5gEejkUN892JrveARt.jpg")
                String profile_path,

                @Schema(description = "제작 부서", example = "Directing")
                String department,

                @Schema(description = "담당 역할", example = "Director")
                String job
        ) {
            public MovieCrew toMovieCrewEntity() {
                return new MovieCrew(job);
            }

            public People toPeopleEntity() {
                return People.builder()
                        .imageUrl(profile_path)
                        .name(name)
                        .tmdbId(id)
                        .build();
            }
        }
    }
}