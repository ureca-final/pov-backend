package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.curation.domain.CurationCategory;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "큐레이션 영화 응답 DTO")
public record ReadCurationMoviesResponse(
        @Schema(description = "큐레이션 응답 DTO")
        ReadCurationResponse readCurationResponse,

        @Schema(description = "큐레이션에 저장될 영화들 Id", example = "[2, 5, 10]")
        Set<Long> movieIds
) {}