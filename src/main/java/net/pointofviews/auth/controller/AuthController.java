package net.pointofviews.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
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

    @Override
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<CreateMemberResponse>> signup(@Valid @RequestBody CreateMemberRequest request) {
        CreateMemberResponse response = memberService.signup(request);
        return BaseResponse.created("/api/v1/auth/login", "회원가입이 완료되었습니다.");
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginMemberResponse>> login(@Valid @RequestBody LoginMemberRequest request) {
        LoginMemberResponse response = memberService.login(request);
        return BaseResponse.ok("로그인이 완료되었습니다.", response);
    }
}
