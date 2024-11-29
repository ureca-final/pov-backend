package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.curation.domain.CurationCategory;

import java.time.LocalDateTime;

@Schema(description = "큐레이션 응답 DTO")
public record ReadCurationResponse(
        @Schema(description = "큐레이션 ID", example = "1")
        Long id,

        @Schema(description = "큐레이션 주제", example = "Top Action Movies")
        String theme,

        @Schema(description = "큐레이션 카테고리", example = "GENRE", allowableValues = {"ACTOR", "GENRE", "DIRECTOR", "AWARD", "RELEASE", "OTHER", "COUNTRY"})
        CurationCategory category,

        @Schema(description = "큐레이션 제목", example = "Best Action Movies of the Year")
        String title,

        @Schema(description = "큐레이션 설명", example = "Top-rated action movies curated for action lovers.")
        String description,

        @Schema(description = "큐레이션 시작 시간", example = "2024-11-22T10:00:00")
        LocalDateTime startTime
) {}