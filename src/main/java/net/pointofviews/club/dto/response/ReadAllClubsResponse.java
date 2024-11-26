package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "공개 클럽 전체 조회 응답 DTO")
public record ReadAllClubsResponse(
        @Schema(description = "클럽 ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID clubId,

        @Schema(description = "클럽 이름", example = "영화광들")
        String clubName,

        @Schema(description = "클럽 생성일", example = "2023-11-22T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "참가자 수", example = "50")
        int participant,

        @Schema(description = "최대 참가자 수", example = "100")
        int maxParticipant
) {}