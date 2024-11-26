package net.pointofviews.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;
import net.pointofviews.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberSpecification {
    private final MemberService memberService;

    @Override
    @DeleteMapping("")
    public ResponseEntity<BaseResponse<Void>> withdraw() {
        memberService.deleteMember();
        return BaseResponse.ok("회원 탈퇴가 완료되었습니다.");
    }

    @Override
    @PutMapping("/profiles/genres")
    public ResponseEntity<BaseResponse<PutMemberGenreListResponse>> putGenres(PutMemberGenreListRequest request) {
        PutMemberGenreListResponse response = memberService.updateGenre(request);
        return BaseResponse.ok("장르 설정이 완료되었습니다.", response);
    }

    @Override
    @PutMapping("/profiles/image")
    public ResponseEntity<BaseResponse<PutMemberImageResponse>> putImage(@Valid PutMemberImageRequest request) {
        PutMemberImageResponse response = memberService.updateImage(request);
        return BaseResponse.ok("프로필 이미지가 변경되었습니다.", response);
    }

    @Override
    @PutMapping("/profiles/nickname")
    public ResponseEntity<BaseResponse<PutMemberNicknameResponse>> putNickname(@Valid PutMemberNicknameRequest request) {
        PutMemberNicknameResponse response = memberService.updateNickname(request);
        return BaseResponse.ok("닉네임이 변경되었습니다.", response);
    }

    @Override
    @PutMapping("/notice")
    public ResponseEntity<BaseResponse<PutMemberNoticeResponse>> putNotice(@Valid PutMemberNoticeRequest request) {
        PutMemberNoticeResponse response = memberService.updateNotice(request);
        return BaseResponse.ok("알림 설정이 변경되었습니다.", response);
    }
}