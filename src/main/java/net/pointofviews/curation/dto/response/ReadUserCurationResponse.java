package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.club.dto.response.ReadClubMovieResponse;
import org.springframework.data.domain.Slice;

@Schema(description = "큐레이션 정보 응답 DTO")
public record ReadUserCurationResponse(
        @Schema(description = "큐레이션 제목", example = "죽기 전에 꼭 봐야할 영화 100선")
        String curationTitle,

        @Schema(description = "큐레이션 영화 정보 응답 리스트")
        Slice<ReadUserCurationMovieResponse> curationMovies
) {}