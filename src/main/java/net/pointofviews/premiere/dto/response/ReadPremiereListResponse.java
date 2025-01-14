package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "시사회 정보 목록 DTO")
public record ReadPremiereListResponse(

        @Schema(description = "시사회 전체 목록")
        List<ReadPremiereResponse> premieres
) {
}
