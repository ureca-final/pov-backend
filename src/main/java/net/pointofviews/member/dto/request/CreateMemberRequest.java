package net.pointofviews.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "회원가입 요청 DTO")
public record CreateMemberRequest(
        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank @Email
        String email,

        @Schema(description = "닉네임", example = "nickname")
        @NotBlank
        String nickname,

        @Schema(description = "생년월일", example = "2000-01-01")
        @NotNull
        LocalDate birth,

        @Schema(description = "소셜 로그인 타입", example = "KAKAO")
        @NotBlank
        String socialType,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
        String profileImage
) {}
