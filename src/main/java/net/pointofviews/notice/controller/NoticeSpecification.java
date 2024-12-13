package net.pointofviews.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.notice.dto.request.CreateNoticeTemplateRequest;
import net.pointofviews.notice.dto.request.SendNoticeRequest;
import net.pointofviews.notice.dto.response.CreateNoticeTemplateResponse;
import net.pointofviews.notice.dto.response.ReadNoticeResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Notice", description = "알림 관련 API")
public interface NoticeSpecification {

    @Operation(summary = "알림 템플릿 생성", description = "새로운 알림 템플릿을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                    {
                      "message": "알림 템플릿 생성에 실패했습니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateNoticeTemplateResponse>> createNoticeTemplate(
            @Valid @RequestBody CreateNoticeTemplateRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    );

    @Operation(summary = "알림 발송", description = "알림 템플릿을 기반으로 알림을 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "템플릿 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                    {
                      "message": "존재하지 않는 알림 템플릿입니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> sendNotice(@Valid @RequestBody SendNoticeRequest request);

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<BaseResponse<List<ReadNoticeResponse>>> readNotices(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    );

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                    {
                      "message": "존재하지 않는 알림입니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> putNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    );
}
