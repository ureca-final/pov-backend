package net.pointofviews.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.notice.domain.NoticeType;

import java.time.LocalDateTime;

public record ReadNoticeResponse(
        @Schema(description = "알림 ID")
        Long id,

        @Schema(description = "알림 제목")
        String title,

        @Schema(description = "알림 내용")
        String content,

        @Schema(description = "알림 유형")
        NoticeType noticeType,

        @Schema(description = "읽음 여부")
        boolean isRead,

        @Schema(description = "생성일시")
        LocalDateTime createdAt,

        @Schema(description = "리뷰 ID")
        Long reviewId
) {
}
