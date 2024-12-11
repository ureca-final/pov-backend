package net.pointofviews.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.notice.domain.NoticeType;

import java.time.LocalDateTime;

public record CreateNoticeTemplateResponse(
        @Schema(description = "템플릿 ID")
        Long id,

        @Schema(description = "알림 제목")
        String title,

        @Schema(description = "알림 내용 템플릿")
        String content,

        @Schema(description = "알림 유형")
        NoticeType noticeType,

        @Schema(description = "알림 설명")
        String description,

        @Schema(description = "활성화 여부")
        boolean isActive,

        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
}
