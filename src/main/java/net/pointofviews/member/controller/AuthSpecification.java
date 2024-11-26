package net.pointofviews.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthSpecification {
    // 회원가입
    @Tag(name = "Auth", description = "회원가입 관련 API")
    @Operation(summary = "회원가입", description = "💡새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "⭕ CREATED",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "회원가입이 완료되었습니다.",
                                      "data": {
                                        "id": "123e4567-e89b-12d3-a456-426614174000",
                                        "email": "user@example.com",
                                        "nickname": "nickname"
                                      }
                                    }""")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "회원가입에 실패했습니다.",
                                    }""")
                    )
            ),
    })
    ResponseEntity<BaseResponse<CreateMemberResponse>> signup(@Valid @RequestBody CreateMemberRequest request);

    // 로그인
    @Tag(name = "Auth", description = "로그인 관련 API")
    @Operation(summary = "로그인", description = "💡회원 로그인을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                  "message": "로그인이 완료되었습니다.",
                                  "data": {
                                    "id": "123e4567-e89b-12d3-a456-426614174000",
                                    "email": "user@example.com",
                                    "nickname": "nickname",
                                    "role": "USER"
                                  }
                                }""")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "로그인에 실패했습니다.",
                                }""")
                    )
            ),
    })
    ResponseEntity<BaseResponse<LoginMemberResponse>> login(@Valid @RequestBody LoginMemberRequest request);
}
