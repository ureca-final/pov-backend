package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "TMDB 영화 출연진 및 제작진 정보 응답 DTO")
public record SearchCreditApiResponse(
        @Schema(description = "영화 ID", example = "27205")
        int id,

        @Schema(description = "출연진 목록")
        List<CastResponse> cast,

        @Schema(description = "제작진 목록")
        List<CrewResponse> crew
) {

    @Schema(description = "출연진 정보")
    public record CastResponse(
            @Schema(description = "성별 (1: 여성, 2: 남성, null: 알 수 없음)", example = "2")
            Integer gender,

            @Schema(description = "출연진 ID", example = "6193")
            int id,

            @Schema(description = "출연진 이름", example = "Leonardo DiCaprio")
            String name,

            @Schema(description = "출연진 원어 이름", example = "Leonardo DiCaprio")
            String original_name,

            @Schema(description = "출연진 프로필 이미지 경로", example = "/wo2hJpn04vbtmh0B9utCFdsQhxM.jpg")
            String profile_path,

            @Schema(description = "출연진 캐스트 ID", example = "4")
            int cast_id,

            @Schema(description = "출연진 배역 이름", example = "Dom Cobb")
            String character,

            @Schema(description = "출연진 순서", example = "0")
            int order
    ) {
    }

    @Schema(description = "제작진 정보")
    public record CrewResponse(
            @Schema(description = "성별 (1: 여성, 2: 남성, null: 알 수 없음)", example = "2")
            Integer gender,

            @Schema(description = "제작진 ID", example = "525")
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
    }
}
