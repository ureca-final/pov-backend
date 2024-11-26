package net.pointofviews.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 닉네임 수정 응답 DTO")
public record PutMemberNicknameResponse(
        @Schema(description = "수정된 닉네임", example = "newNickname")
        String nickname
) {}
