package net.pointofviews.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 알림 설정 수정 응답 DTO")
public record PutMemberNoticeResponse(
        @Schema(description = "수정된 알림 활성화 여부", example = "true")
        boolean isNoticeActive
) {}
