package net.pointofviews.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "회원가입 응답 DTO")
public record CreateMemberResponse(
        @Schema(description = "회원 ID")
        UUID id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "닉네임", example = "nickname")
        String nickname
) {
}
