package net.pointofviews.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.auth.utils.JwtProvider;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSpecification {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<CreateMemberResponse>> signup(@Valid @RequestBody CreateMemberRequest request) {
        CreateMemberResponse response = memberService.signup(request);
        return BaseResponse.created("/api/v1/auth/login", "회원가입이 완료되었습니다.");
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginMemberResponse>> login(@Valid @RequestBody LoginMemberRequest request, HttpServletResponse response) {
        LoginMemberResponse loginResponse = memberService.login(request);

        // 토큰 생성 (AT: 1시간, RT: 2주)
        String accessToken = jwtProvider.createToken(loginResponse.id(), 3600000);
        String refreshToken = jwtProvider.createToken(loginResponse.id(), 1209600000);

        // Access Token은 Authorization 헤더에 설정
        response.setHeader("Authorization", accessToken);

        // Refresh Token은 보안 쿠키로 설정
        String cookieRefreshToken = refreshToken.replace(" ", "%20");

        Cookie refreshTokenCookie = new Cookie("refresh-token", cookieRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);  // HTTPS only
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(1209600);  // 2주
        response.addCookie(refreshTokenCookie);


        return BaseResponse.ok("로그인이 완료되었습니다.", loginResponse);
    }
}
