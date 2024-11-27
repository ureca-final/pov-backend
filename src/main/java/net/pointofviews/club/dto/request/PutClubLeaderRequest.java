package net.pointofviews.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "클럽장 변경 요청 DTO")
public record PutClubLeaderRequest(
        @Schema(description = "새로운 클럽장 ID")
        @NotNull(message = "새로운 클럽장 ID는 필수입니다.")
        UUID newLeaderId
) {}