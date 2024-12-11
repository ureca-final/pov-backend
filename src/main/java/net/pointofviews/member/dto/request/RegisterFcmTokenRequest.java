package net.pointofviews.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegisterFcmTokenRequest(
        @Schema(description = "FCM 토큰", example = "eKy...8dw")
        @NotBlank
        String fcmToken
) {
}
