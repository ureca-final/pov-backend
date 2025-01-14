package net.pointofviews.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CheckLoginResponse;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.utils.JwtProvider;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthSpecification {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    // Access Token 유효 시간 (1분, 1시간, 2주)
    private static final long EXPIRATION_TIME_1M = 60000;
    private static final long EXPIRATION_TIME_1H = EXPIRATION_TIME_1M * 60;
    private static final long EXPIRATION_TIME_2W = 1209600000;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<CheckLoginResponse>> signup(
            @Valid @RequestBody CreateMemberRequest request,
            HttpServletResponse response) {

        // 회원가입
        CreateMemberResponse signupResponse = memberService.signup(request);

        // 회원가입후 로그인 처리
        LoginMemberRequest loginRequest = new LoginMemberRequest(
                request.email(),
                request.socialType()
        );

        return login(loginRequest, response);  // 기존 로그인 메서드 재사용
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<CheckLoginResponse>> login(@Valid @RequestBody LoginMemberRequest request, HttpServletResponse response) {
        CheckLoginResponse loginResponse = memberService.login(request);

        // 회원이 존재하는 경우에만 토큰 생성 및 설정
        if (loginResponse.exists() && loginResponse.memberInfo() != null) {
            // 토큰 생성 (AT: 1시간, RT: 2주)
            String accessToken = jwtProvider.createToken(loginResponse.memberInfo().id(), EXPIRATION_TIME_1H);
            String refreshToken = jwtProvider.createToken(loginResponse.memberInfo().id(), EXPIRATION_TIME_2W);

            // Access Token은 Authorization 헤더에 설정
            response.setHeader("Authorization", accessToken);

            // Refresh Token은 보안 쿠키로 설정
            String cookieRefreshToken = refreshToken.replace(" ", "%20");

            Cookie refreshTokenCookie = new Cookie("refresh-token", cookieRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setAttribute("SameSite", "None");
            refreshTokenCookie.setMaxAge((int) EXPIRATION_TIME_2W);
            response.addCookie(refreshTokenCookie);
        }

        return BaseResponse.ok(loginResponse.exists() ? "로그인이 완료되었습니다." : "가입되지 않은 이메일입니다.",
                loginResponse);
    }
}
