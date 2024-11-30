package net.pointofviews.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface MemberSpecification {
	// 회원 탈퇴
	@Tag(name = "Member", description = "회원 탈퇴 관련 API")
	@Operation(summary = "회원 탈퇴", description = "💡회원 정보를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					  "message": "탈퇴가 완료되었습니다."
					}""")
			)
		),
		@ApiResponse(responseCode = "400", description = "❌ FAIL",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					  "message": "탈퇴에 실패했습니다."
					}""")
			)
		)
	})
	ResponseEntity<BaseResponse<Void>> withdraw();

	// 회원 선호 장르 변경
	@Tag(name = "Member", description = "회원 장르 변경 관련 API")
	@Operation(summary = "회원 장르 변경", description = "💡회원의 선호 장르를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
		),
		@ApiResponse(responseCode = "404", description = "❌ FAIL",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					  "message": "잘못된 장르(Name: 시사교양)를 요청했습니다."
					}
					""")
			)
		)
	})
	ResponseEntity<BaseResponse<PutMemberGenreListResponse>> putGenres(
		@AuthenticationPrincipal(expression = "member") Member loginMember,
		@Valid @RequestBody PutMemberGenreListRequest request
	);

	// 회원 프로필 이미지 변경
	@Tag(name = "Member", description = "회원 프로필 이미지 변경 관련 API")
	@Operation(summary = "프로필 이미지 변경", description = "💡회원의 프로필 이미지를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
		),
		@ApiResponse(responseCode = "404", description = "❌ FAIL",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					  "message": "프로필 이미지 변경에 실패했습니다."
					}""")
			)
		)
	})
	ResponseEntity<BaseResponse<PutMemberImageResponse>> putImage(@Valid @RequestBody PutMemberImageRequest request);

	// 회원 닉네임 변경
	@Tag(name = "Member", description = "회원 닉네임 변경 관련 API")
	@Operation(summary = "닉네임 변경", description = "💡회원의 닉네임을 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
		),
		@ApiResponse(responseCode = "409", description = "❌ FAIL",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					    {
					      "message": "닉네임 중복으로 인해 변경이 실패했습니다."
					    }
					""")
			)
		)
	})
	ResponseEntity<BaseResponse<PutMemberNicknameResponse>> putNickname(
		@AuthenticationPrincipal(expression = "member") Member loginMember,
		@Valid @RequestBody PutMemberNicknameRequest request
	);

	// 회원 알림 설정 변경
	@Tag(name = "Member", description = "회원 알림 설정 변경 관련 API")
	@Operation(summary = "알림 설정 변경", description = "💡회원의 알림 설정을 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
		),
		@ApiResponse(responseCode = "404", description = "❌ FAIL",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					  "message": "알림 설정 변경에 실패했습니다."
					}""")
			)
		)
	})
	ResponseEntity<BaseResponse<PutMemberNoticeResponse>> putNotice(@Valid @RequestBody PutMemberNoticeRequest request);
}
