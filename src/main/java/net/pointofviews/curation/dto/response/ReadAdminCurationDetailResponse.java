package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

@Schema(description = "관리자 큐레이션 상세 응답 DTO")
public record ReadAdminCurationDetailResponse(
        @Schema(description = "관리자 큐레이션 응답 DTO")
        ReadAdminCurationResponse readAdminCurationResponse,

        @Schema(description = "큐레이션에 저장 될 영화 리스트")
        List<ReadAdminCurationMovieResponse> readAdminCurationMovieResponseList
) {}