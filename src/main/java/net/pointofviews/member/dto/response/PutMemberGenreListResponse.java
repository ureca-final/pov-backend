package net.pointofviews.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "회원 장르 수정 응답 DTO")
public record PutMemberGenreListResponse(
        @Schema(description = "수정된 선호 장르 목록", example = "[\"ACTION\", \"ROMANCE\"]")
        List<String> genres
) {}
