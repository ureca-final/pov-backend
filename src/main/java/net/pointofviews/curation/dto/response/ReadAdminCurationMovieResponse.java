package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "큐레이션 영화 정보 응답 DTO")
public record ReadAdminCurationMovieResponse(
        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        LocalDate released
) {
        public ReadAdminCurationMovieResponse(String title, LocalDate released) {
                this.title = title;
                this.released = released;
        }
}
