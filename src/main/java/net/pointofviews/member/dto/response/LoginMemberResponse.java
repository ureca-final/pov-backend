package net.pointofviews.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "로그인 응답 DTO")
public record LoginMemberResponse(
        @Schema(description = "회원 ID")
        UUID id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "닉네임", example = "nickname")
        String nickname,

        @Schema(description = "회원 권한", example = "USER")
        String role,

        @Schema(description = "접근 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken
) {}
