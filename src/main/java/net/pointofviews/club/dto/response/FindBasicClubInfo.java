package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "클럽 기본 정보 DTO")
public record FindBasicClubInfo(
        @Schema(description = "클럽 이름", example = "00즈")
        String name,

        @Schema(description = "클럽 설명", example = "영화를 좋아하는 00년생들")
        String description,

        @Schema(description = "클럽 대표 이미지", example = "https://example.com/image.jpg")
        String image,

        @Schema(description = "클럽 공개 여부", example = "true")
        boolean isPublic,

        @Schema(description = "참가자 수", example = "3")
        Long participant,

        @Schema(description = "최대 참가자 수", example = "100")
        Integer maxParticipants,

        @Schema(description = "이 클럽의 북마크 수", example = "5")
        Long movieCount
) {}
