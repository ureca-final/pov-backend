package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "전체 큐레이션 응답 리스트 DTO")
public record ReadAdminAllCurationListResponse(
        @Schema(description = "전체 큐레이션 응답 리스트")
        List<ReadAdminAllCurationResponse> curations
) {}