package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "클럽 멤버 정보 응답 DTO")
public record ReadClubMemberResponse(
        @Schema(description = "닉네임", example = "nickname")
        String nickname,

        @Schema(description = "프로필 이미지", example = "https://example.com/image.jpg")
        String profileImage,

        @Schema(description = "리더 여부", example = "false")
        boolean isLeader
) {}
