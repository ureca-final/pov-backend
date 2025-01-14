package net.pointofviews.premiere.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.request.DeleteEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.dto.response.ReadMyEntryListResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Entry", description = "시사회 응모 관련 API")
public interface EntrySpecification {

    @Operation(
            summary = "시사회 응모",
            description = "사용자가 시사회에 응모할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 응모 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회 응모가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회(Id: 123)가 존재하지 않습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미 응모한 시사회입니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회 응모 최대 인원 수를 초과했습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateEntryResponse>> createEntry(
            MemberDetailsDto loginMember,
            @Parameter(description = "응모할 시사회 ID", example = "123") Long premiereId,
            CreateEntryRequest request
    ) throws IllegalAccessException, InterruptedException;

    @Operation(
            summary = "시사회 응모",
            description = "사용자가 시사회에 응모할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 응모 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회 응모가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회(Id: 123)가 존재하지 않습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미 응모한 시사회입니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "시사회 응모 최대 인원 수를 초과했습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "시사회 응모 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateEntryResponse>> createEntry2(
            MemberDetailsDto loginMember,
            @Parameter(description = "응모할 시사회 ID", example = "123") Long premiereId,
            CreateEntryRequest request
    ) throws IllegalAccessException, InterruptedException;

    @Operation(
            summary = "시사회 응모 취소",
            description = "응모한 시사회를 취소할 수 있는 api"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시사회 응모 취소 성공"),
            @ApiResponse(responseCode = "404", description = "시사회 응모 취소 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "응모 내역이 존재하지 않습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "시사회 응모 취소 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "응모한 사용자가 아닙니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> cancelEntry(
            Member loginMember,
            @Parameter(description = "응모한 시사회 ID", example = "123") Long premiereId,
            DeleteEntryRequest request
    );

    @Operation(
            summary = "내 티켓팅 내역 조회",
            description = "사용자가 응모한 모든 시사회 결제 내역을 최신 순으로 조회하는 API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 티켓팅 내역 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "아직 응모한 시사회 없음"
            )
    })
    ResponseEntity<BaseResponse<ReadMyEntryListResponse>> readMyEntry(Member loginMember);
}
