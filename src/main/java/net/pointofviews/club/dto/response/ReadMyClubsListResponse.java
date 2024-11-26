package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "내 클럽 조회 리스트 응답 DTO")
public record ReadMyClubsListResponse(
        @Schema(description = "내 클럽 리스트")
        List<ReadMyClubsResponse> clubs
) {}