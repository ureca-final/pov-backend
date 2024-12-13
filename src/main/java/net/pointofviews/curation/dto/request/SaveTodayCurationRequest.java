package net.pointofviews.curation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import org.springframework.data.domain.Slice;

import java.util.List;

public record SaveTodayCurationRequest(
        @Schema(description = "큐레이션 제목", example = "죽기 전에 꼭 봐야할 영화 100선")
        String curationTitle,

        @Schema(description = "큐레이션 영화 정보 응답 리스트")
        List<ReadUserCurationMovieResponse> curationMovies
) {
}
