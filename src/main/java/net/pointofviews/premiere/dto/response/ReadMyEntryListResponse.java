package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "내 티켓팅 내역 목록 DTO")
public record ReadMyEntryListResponse(

        @Schema(description = "내 티켓팅 내역 목록")
        List<ReadEntryResponse> entry
) {
}
