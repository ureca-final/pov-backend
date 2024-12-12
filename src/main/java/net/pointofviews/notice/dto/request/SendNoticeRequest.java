package net.pointofviews.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SendNoticeRequest(
        @Schema(description = "알림 템플릿 ID")
        @NotNull
        Long noticeTemplateId,

        @Schema(description = "템플릿 치환 변수", example = "{\"genre\": \"액션\", \"movieTitle\": \"스파이더맨\"}")
        @NotNull
        Map<String, String> templateVariables
) {}
