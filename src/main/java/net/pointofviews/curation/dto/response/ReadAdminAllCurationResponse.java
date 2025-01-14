package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "전체 큐레이션 응답 DTO")
public record ReadAdminAllCurationResponse(
        @Schema(description = "큐레이션 ID", example = "1")
        Long id,

        @Schema(description = "큐레이션 제목", example = "Best Action Movies of the Year")
        String title,

        @Schema(description = "큐레이션 시작 시간", example = "2024-11-22T10:00:00")
        LocalDateTime startTime
) {}