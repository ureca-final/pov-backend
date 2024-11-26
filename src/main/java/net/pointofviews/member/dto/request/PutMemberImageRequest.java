package net.pointofviews.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 프로필 이미지 수정 요청 DTO")
public record PutMemberImageRequest(
        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
        @NotBlank
        String profileImage
) {}
