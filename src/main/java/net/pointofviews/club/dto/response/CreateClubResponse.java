package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "클럽 생성 응답 DTO")
public record CreateClubResponse(
        @Schema(description = "클럽 ID")
        UUID clubId,

        @Schema(description = "클럽 이름", example = "영화 감상 모임")
        String name,

        @Schema(description = "클럽 설명", example = "함께 영화를 보고 이야기를 나누는 모임입니다.")
        String description,

        @Schema(description = "최대 인원 수", example = "10")
        int maxParticipants,

        @Schema(description = "현재 인원 수", example = "1")
        int currentMembers,

        @Schema(description = "공개 여부", example = "true")
        boolean isPublic,

        @Schema(description = "클럽 선호 장르 목록", example = "[\"액션\", \"로맨스\"]")
        List<String> clubFavorGenre
) {}
