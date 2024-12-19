package net.pointofviews.movie.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.people.domain.People;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "영화 수정 요청 DTO")
public record PutMovieRequest(
        @Schema(description = "영화 제목", example = "다크 나이트")
        String title,

        @Schema(description = "영화 장르 목록", example = "[\"스릴러\", \"라마\"]")
        List<String> genre,

        @Schema(description = "국가", example = "[\"영국\", \"미국\"]")
        List<String> country,

        @Schema(description = "개봉일", example = "2008-07-18")
        LocalDate release,

        @Schema(description = "줄거리", example = "조커라는 위협이 그의 신비한 과거에서 등장하면서, 고담 시민들에게 혼란과 혼돈을 초래합니다. 다크 나이트는 정의를 지키기 위해 그의 심리적, 신체적 한계를 시험해야 합니다.")
        String plot,

        @Schema(description = "영화 관련인 목록")
        UpdateMoviePeopleRequest peoples
) {
    @Schema(description = "TMDB 영화 출연진 및 제작진 업데이트 요청 DTO")
    public record UpdateMoviePeopleRequest(
            @Schema(description = "출연진 목록")
            List<CastRequest> cast,

            @Schema(description = "제작진 목록")
            List<CrewRequest> crew
    ) {
        @Schema(description = "출연진 업데이트 요청")
        public record CastRequest(
                @Schema(description = "서버 출연진 ID", example = "10")
                @NotNull
                Long id,

                @Schema(description = "tmdb 출연진 ID", example = "12345")
                @NotNull
                Integer tmdbId,

                @Schema(description = "출연진 이름", example = "Christian Bale")
                String name,

                @Schema(description = "출연진 프로필 이미지 경로", example = "/abc123.jpg")
                String profile_path,

                @Schema(description = "출연진 배역 이름", example = "Bruce Wayne / Batman")
                String character,

                @Schema(description = "출연진 순서", example = "1")
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
                        .tmdbId(tmdbId)
                        .build();
            }
        }

        @Schema(description = "제작진 업데이트 요청")
        public record CrewRequest(
                @Schema(description = "서버 제작진 ID", example = "20")
                @NotNull
                Long id,

                @Schema(description = "tmdb 제작진 ID", example = "54321")
                @NotNull
                Integer tmdbId,

                @Schema(description = "제작진 이름", example = "Christopher Nolan")
                String name,

                @Schema(description = "제작진 원어 이름", example = "Christopher Nolan")
                String original_name,

                @Schema(description = "제작진 프로필 이미지 경로", example = "/ghi789.jpg")
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
                        .tmdbId(tmdbId)
                        .build();
            }
        }
    }
}
