package net.pointofviews.curation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Curation", description = "관리자 영화 추천 큐레이션 API")
public interface CurationSpecification {

    @Operation(summary = "모든 큐레이션 조회", description = "관리자가 생성한 모든 큐레이션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadCurationListResponse>> readAllCurations();

    @Operation(summary = "특정 큐레이션 조회", description = "큐레이션 ID를 기반으로 특정 큐레이션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 큐레이션 입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadCurationResponse>> readCuration(
            @PathVariable Long curationId
    );

}