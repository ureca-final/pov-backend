package net.pointofviews.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.dto.request.PutMemberGenreListRequest;
import net.pointofviews.member.dto.request.PutMemberNicknameRequest;
import net.pointofviews.member.dto.request.PutMemberNoticeRequest;
import net.pointofviews.member.dto.request.RegisterFcmTokenRequest;
import net.pointofviews.member.dto.response.PutMemberGenreListResponse;
import net.pointofviews.member.dto.response.PutMemberImageResponse;
import net.pointofviews.member.dto.response.PutMemberNicknameResponse;
import net.pointofviews.member.dto.response.PutMemberNoticeResponse;
import net.pointofviews.member.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/members")
public class MemberController implements MemberSpecification {
    private final MemberService memberService;

    @Override
    @PostMapping("/logout")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BaseResponse<Void>> logout(
            @AuthenticationPrincipal MemberDetailsDto memberDetails,
            HttpServletResponse response
    ) {
        memberService.logout(memberDetails.member());

        // Refresh Token 쿠키 제거
        Cookie refreshTokenCookie = new Cookie("refresh-token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        return BaseResponse.ok("로그아웃이 완료되었습니다.");
    }

    @Override
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> withdraw(@AuthenticationPrincipal MemberDetailsDto memberDetails) {
        memberService.deleteMember(memberDetails.member());
        return BaseResponse.ok("회원 탈퇴가 완료되었습니다.");
    }

    @Override
    @PutMapping("/profiles/genres")
    public ResponseEntity<BaseResponse<PutMemberGenreListResponse>> putGenres(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @Valid @RequestBody PutMemberGenreListRequest request
    ) {
        PutMemberGenreListResponse response = memberService.updateGenre(loginMember, request);

        return BaseResponse.ok("장르 설정이 완료되었습니다.", response);
    }

    @Override
    @PutMapping(value = "/profiles/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PutMemberImageResponse>> putProfileImage(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestPart(value = "profileImage") MultipartFile file
    ) {
        PutMemberImageResponse response = memberService.updateProfileImage(loginMember, file);

        return BaseResponse.ok("프로필 이미지가 변경되었습니다.", response);
    }

    @Override
    @PutMapping("/profiles/nickname")
    public ResponseEntity<BaseResponse<PutMemberNicknameResponse>> putNickname(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @Valid @RequestBody PutMemberNicknameRequest request
    ) {
        PutMemberNicknameResponse response = memberService.updateNickname(loginMember, request);

        return BaseResponse.ok("닉네임이 변경되었습니다.", response);
    }

    @Override
    @PutMapping("/notice")
    public ResponseEntity<BaseResponse<PutMemberNoticeResponse>> putNotice(@Valid PutMemberNoticeRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        PutMemberNoticeResponse response = memberService.updateNotice(memberDetailsDto.member(), request);
        return BaseResponse.ok("알림 설정이 변경되었습니다.", response);
    }

    @PostMapping("/fcmToken")
    @Override
    public ResponseEntity<BaseResponse<Void>> registerFcmToken(
            @Valid @RequestBody RegisterFcmTokenRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        memberService.registerFcmToken(memberDetailsDto.member(), request.fcmToken());
        return BaseResponse.ok("FCM 토큰이 성공적으로 등록되었습니다.");
    }
}