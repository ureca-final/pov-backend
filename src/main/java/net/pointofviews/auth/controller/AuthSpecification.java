package net.pointofviews.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthSpecification {
    // 회원가입
    @Tag(name = "Auth", description = "회원가입 관련 API")
    @Operation(summary = "회원가입", description = "💡새로운 회원을 등록하고 자동으로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ CREATED"
            ),
            @ApiResponse(responseCode = "400", description = "❌ BAD REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "잘못된 소셜 로그인 타입입니다."
                                }""")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "❌ CONFLICT",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "이미 존재하는 이메일입니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<LoginMemberResponse>> signup(@Valid @RequestBody CreateMemberRequest request, HttpServletResponse response);

    // 로그인
    @Tag(name = "Auth", description = "로그인 관련 API")
    @Operation(summary = "로그인", description = "💡회원 로그인을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
            ),
            @ApiResponse(responseCode = "400", description = "❌ BAD REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "잘못된 소셜 로그인 타입입니다."
                                }""")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "❌ CONFLICT",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "이미 존재하는 이메일입니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<LoginMemberResponse>> login(@Valid @RequestBody LoginMemberRequest request, HttpServletResponse response);
}
