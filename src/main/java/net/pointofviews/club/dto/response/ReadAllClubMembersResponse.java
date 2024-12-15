package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "클럽 멤버 정보 응답 DTO")
public record ReadAllClubMembersResponse(

        @Schema(description = "클럽 멤버 목록")
        List<ClubMemberResponse> clubMember
) {

}