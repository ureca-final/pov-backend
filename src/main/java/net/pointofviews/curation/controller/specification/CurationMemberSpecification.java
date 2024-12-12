package net.pointofviews.curation.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Curation", description = "사용자 영화 추천 큐레이션 API")
public interface CurationMemberSpecification {

    @Operation(summary = "사용자 스케줄링 된 큐레이션 조회", description = "사용자가 스케줄링 된 큐레이션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadUserCurationListResponse>> readScheduledCurations(Pageable pageable);
}