package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Slice;

@Schema(description = "클럽 검색 리스트 응답 DTO")
public record SearchClubsListResponse(
        @Schema(description = "클럽 리스트")
        Slice<ReadAllClubsResponse> clubs
) {}