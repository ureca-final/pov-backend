package net.pointofviews.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "회원 장르 수정 요청 DTO")
public record PutMemberGenreListRequest(
        @Schema(description = "선호 장르 목록", example = "[\"ACTION\", \"ROMANCE\"]")
        List<String> genres
) {}
