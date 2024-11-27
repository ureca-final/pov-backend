package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "클럽장 변경 응답 DTO")
public record PutClubLeaderResponse(
        @Schema(description = "클럽 ID")
        UUID clubId,

        @Schema(description = "새로운 클럽장 ID")
        UUID newLeaderId,

        @Schema(description = "새로운 클럽장 닉네임", example = "새클럽장")
        String newLeaderNickname
) {}