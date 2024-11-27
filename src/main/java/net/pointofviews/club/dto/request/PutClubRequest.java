package net.pointofviews.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

@Schema(description = "클럽 수정 요청 DTO")
public record PutClubRequest(
        @Schema(description = "클럽 이름", example = "영화 감상 모임")
        String name,

        @Schema(description = "클럽 설명", example = "함께 영화를 보고 이야기를 나누는 모임입니다.")
        String description,

        @Schema(description = "최대 참여 인원", example = "10")
        @Min(2) @Max(1000)
        Integer maxParticipants,

        @Schema(description = "공개 여부", example = "true")
        boolean isPublic,

        @Schema(description = "클럽 선호 장르 목록", example = "[\"ACTION\", \"ROMANCE\"]")
        List<String> clubFavorGenre
) {}