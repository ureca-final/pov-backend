package net.pointofviews.club.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 클럽 조회 리스트 응답 DTO")
public record ReadMyClubsListResponse(
        @Schema(description = "내 클럽 리스트")
        List<ReadClubDetailsResponse> clubs
) {}