package net.pointofviews.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원 알림 설정 수정 요청 DTO")
public record PutMemberNoticeRequest(
        @Schema(description = "알림 활성화 여부", example = "true")
        @NotNull
        boolean isNoticeActive
) {}
