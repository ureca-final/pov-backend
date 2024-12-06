package net.pointofviews.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "클럽 생성 요청 DTO")
public record CreateClubRequest(
        @Schema(description = "클럽 이름", example = "영화 감상 모임")
        @NotBlank
        String name,

        @Schema(description = "클럽 설명", example = "함께 영화를 보고 이야기를 나누는 모임입니다.")
        @NotBlank
        String description,

        @Schema(description = "최대 참여 인원", example = "10")
        @Min(2) @Max(1000)
        Integer maxParticipants,

        @Schema(description = "공개 여부", example = "true")
        boolean isPublic,

        @Schema(description = "클럽 선호 장르 목록", example = "[\"액션\", \"로맨스\"]")
        List<String> clubFavorGenre
) {}