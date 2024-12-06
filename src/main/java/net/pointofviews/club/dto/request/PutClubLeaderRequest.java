package net.pointofviews.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "클럽장 변경 요청 DTO")
public record PutClubLeaderRequest(
        @Schema(description = "새로운 클럽장 이메일", example = "user@example.com")
        @NotNull(message = "새로운 클럽장 이메일은 필수입니다.")
        String newLeaderEmail
) {}