package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "클럽 멤버 목록 응답 DTO")
public record ReadClubMemberListResponse(
        @Schema(description = "멤버 목록")
        List<ReadClubMemberResponse> memberList
) {}
