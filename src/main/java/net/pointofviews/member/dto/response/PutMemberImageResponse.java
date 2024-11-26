package net.pointofviews.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 프로필 이미지 수정 응답 DTO")
public record PutMemberImageResponse(
        @Schema(description = "수정된 프로필 이미지 URL", example = "https://example.com/image.jpg")
        String profileImage
) {}
