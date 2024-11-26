package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "내 클럽 조회 응답 DTO")
public record ReadMyClubsResponse(
        @Schema(description = "클럽 이름", example = "00즈")
        String clubName,

        @Schema(description = "참가자 목록", example = "[\"노지민\", \"박시은\", \"이승희\"]")
        List<String> members,

        @Schema(description = "참가자 수", example = "50")
        int participant,

        @Schema(description = "최대 참가자 수", example = "100")
        int maxParticipant,

        @Schema(description = "클럽 테마", example = "Classic Movies")
        String clubTheme,

        @Schema(description = "클럽 설명", example = "영화를 좋아하는 00년생들")
        String clubDescription,

        @Schema(description = "클럽 공개 여부", example = "true")
        boolean isPublic
) {}