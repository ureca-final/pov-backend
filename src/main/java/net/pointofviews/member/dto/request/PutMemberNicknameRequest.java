package net.pointofviews.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 닉네임 수정 요청 DTO")
public record PutMemberNicknameRequest(
        @Schema(description = "닉네임", example = "newNickname")
        @NotBlank
        String nickname
) {}
