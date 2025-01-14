package net.pointofviews.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

public interface MemberSpecification {
	// 로그아웃
	@Tag(name = "Member", description = "회원 로그아웃 관련 API")
	@Operation(summary = "로그아웃", description = "💡회원의 토큰을 삭제하고 로그아웃합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "⭕ SUCCESS"),
			@ApiResponse(responseCode = "401", description = "❌ FAIL",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요한 서비스입니다."
                }""")
					)
			)
	})
	ResponseEntity<BaseResponse<Void>> logout(
			@AuthenticationPrincipal MemberDetailsDto memberDetails,
			HttpServletResponse response
	);

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
	ResponseEntity<BaseResponse<Void>> withdraw(@AuthenticationPrincipal MemberDetailsDto memberDetails);

	// 회원 선호 장르 변경
	@Tag(name = "Member", description = "회원 장르 변경 관련 API")
	@Operation(summary = "회원 장르 변경", description = "💡회원의 선호 장르를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
		),
		@ApiResponse(responseCode = "400", description = "❌ FAIL",
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
		@ApiResponse(responseCode = "500", description = "❌ FAIL",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					  "message": "S3 업로드 실패: {ex.Message}"
					}
					""")
			)
		)
	})
	ResponseEntity<BaseResponse<PutMemberImageResponse>> putProfileImage(
		@AuthenticationPrincipal(expression = "member") Member loginMember,
		@RequestPart(value = "profileImage") MultipartFile file
	);

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
	ResponseEntity<BaseResponse<PutMemberNoticeResponse>> putNotice(@Valid PutMemberNoticeRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto);

	@Tag(name = "Member", description = "회원 FCM 토큰 관리 API")
	@Operation(summary = "FCM 토큰 등록", description = "회원의 FCM 토큰을 등록합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "등록 성공"),
			@ApiResponse(
					responseCode = "401",
					description = "로그인이 필요한 서비스입니다.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							examples = @ExampleObject(value = """
                {
                    "message": "로그인이 필요한 서비스입니다."
                }
                """)
					)
			)
	})
	ResponseEntity<BaseResponse<Void>> registerFcmToken(
			@Valid @RequestBody RegisterFcmTokenRequest request,
			@AuthenticationPrincipal MemberDetailsDto memberDetailsDto
	);

}
