package net.pointofviews.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 확인 응답 DTO")
public record CheckLoginResponse(
        @Schema(description = "회원 존재 여부")
        boolean exists,

        @Schema(description = "회원 정보 (존재하는 경우에만)")
        LoginMemberResponse memberInfo
) {}