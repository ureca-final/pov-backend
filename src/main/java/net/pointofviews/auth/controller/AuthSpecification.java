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
import net.pointofviews.auth.dto.response.CheckLoginResponse;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthSpecification {
    // 회원가입 (변경 사항 없음)
    @Tag(name = "Auth", description = "회원가입 관련 API")
    @Operation(summary = "회원가입", description = "💡새로운 회원을 등록하고 자동으로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ CREATED"),
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
    ResponseEntity<BaseResponse<CheckLoginResponse>> signup(@Valid @RequestBody CreateMemberRequest request, HttpServletResponse response);

    // 로그인 (수정 필요)
    @Tag(name = "Auth", description = "로그인 관련 API")
    @Operation(summary = "로그인", description = "💡회원 로그인을 처리합니다. 회원이 없는 경우에도 200을 반환하며, exists 필드로 회원 존재 여부를 확인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "회원이 존재하는 경우", value = """
                                    {
                                      "message": "로그인이 완료되었습니다.",
                                      "data": {
                                        "exists": true,
                                        "memberInfo": {
                                          "id": "uuid",
                                          "email": "user@example.com",
                                          "nickname": "nickname",
                                          "birth": "2000-01-01",
                                          "favorGenres": ["로맨스", "코미디"],
                                          "profileImage": "https://example.com/image.jpg",
                                          "role": "USER"
                                        }
                                      }
                                    }"""),
                                    @ExampleObject(name = "회원이 존재하지 않는 경우", value = """
                                    {
                                      "message": "가입되지 않은 이메일입니다.",
                                      "data": {
                                        "exists": false,
                                        "memberInfo": null
                                      }
                                    }""")
                            })
            ),
            @ApiResponse(responseCode = "400", description = "❌ BAD REQUEST",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "잘못된 소셜 로그인 타입입니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<CheckLoginResponse>> login(@Valid @RequestBody LoginMemberRequest request, HttpServletResponse response);
}
