package net.pointofviews.premiere.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.premiere.dto.request.CreatePremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Premiere", description = "시사회 관련 API")
public interface PremiereSpecification {
    @Operation(
            summary = "시사회 목록 조회",
            description = "등록된 모든 시사회 목록을 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList();

    @Operation(
            summary = "시사회 상세 조회",
            description = "시사회 ID를 이용해 특정 시사회의 세부 정보를 조회하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패 - 존재하지 않는 시사회",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 시사회입니다."
                                    }
                                    """)
                    ))

    })
    ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetails(
            @Parameter(
                    description = "조회할 시사회 ID",
                    example = "123"
            )
            Long premiereId);


    @Operation(
            summary = "시사회 응모",
            description = "사용자가 시사회에 응모할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "응모 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회 응모가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "응모 실패 - 존재하지 않는 시사회",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 시사회입니다."
                                    }
                                    """)
                    ))

    })
    ResponseEntity<BaseResponse<Void>> createEntryPremiere(Long premiereId);

    @Operation(
            summary = "시사회 등록",
            description = "관리자가 시사회를 등록할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 등록 성공", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "성공적으로 시사회를 등록했습니다."
                            }
                            """)
            ))
    })
    ResponseEntity<BaseResponse<CreatePremiereRequest>> createPremiere(CreatePremiereRequest premiere);
}
