package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "공개 클럽 전체 조회 리스트 응답 DTO")
public record ReadAllClubsListResponse(
        @Schema(description = "클럽 리스트")
        List<ReadAllClubsResponse> clubs
) {}