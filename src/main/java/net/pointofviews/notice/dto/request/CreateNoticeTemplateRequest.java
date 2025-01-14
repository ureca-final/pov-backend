package net.pointofviews.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.pointofviews.notice.domain.NoticeType;

@Schema(description = "알림 템플릿 생성 요청 DTO")
public record CreateNoticeTemplateRequest(
        @Schema(description = "알림 제목", example = "새로운 영화 리뷰 알림")
        @NotBlank
        String title,

        @Schema(description = "알림 내용 템플릿", example = "{genre}장르의 '{movieTitle}'에 새로운 리뷰가 작성되었습니다")
        @NotBlank
        String content,

        @Schema(description = "알림 유형", example = "REVIEW")
        @NotNull
        NoticeType noticeType,

        @Schema(description = "알림 설명", example = "선호 장르 영화의 새 리뷰 알림")
        String description
) {}
