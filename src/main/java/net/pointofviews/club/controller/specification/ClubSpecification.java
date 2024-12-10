package net.pointofviews.club.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.club.dto.request.*;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

@Tag(name = "Club", description = "클럽 관련 API")
public interface ClubSpecification {

    @Operation(summary = "공개 그룹 전체 조회", description = "모든 공개 클럽 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllClubs();

    @Operation(summary = "그룹 검색", description = "클럽을 검색합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            )
    })
    ResponseEntity<BaseResponse<SearchClubsListResponse>> searchClubs(
            @RequestParam String query,
            Pageable pageable
    );

    @Operation(summary = "클럽 상세 조회", description = "특정 클럽의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 클럽입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadClubDetailsResponse>> readClubDetails(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            Pageable pageable
    );

    @Operation(summary = "내 그룹 조회", description = "사용자가 속한 모든 클럽 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllMyClubs(@AuthenticationPrincipal(expression = "member") Member loginMember);

    @Operation(summary = "클럽 생성", description = "새로운 클럽을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽 생성에 실패했습니다."
                                    }""")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "로그인이 필요한 서비스입니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateClubResponse>> createClub(@Valid @RequestBody CreateClubRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto);

    @Operation(
            summary = "리뷰 이미지 업로드",
            description = "리뷰 작성 시 이미지를 업로드하는 API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 업로드 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미지 업로드 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "지원하지 않는 파일 형식입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateClubImageListResponse>> createClubImages(
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    );

    @Operation(summary = "클럽 이미지 수정", description = "클럽의 이미지를 수정합니다. 클럽장만 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 업로드 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미지 업로드 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "지원하지 않는 파일 형식입니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "클럽장만 가능합니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateClubImageListResponse>> putClubImages(@RequestPart(value = "files") UUID clubId, List<MultipartFile> files, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto);

    @Operation(summary = "클럽 수정", description = "기존 클럽 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "클럽장만 가능합니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "클럽 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽 수정에 실패했습니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<PutClubResponse>> putClub(@PathVariable UUID clubId, @Valid @RequestBody PutClubRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto);

    @Operation(summary = "클럽 탈퇴", description = "클럽을 탈퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽 탈퇴가 완료되었습니다."
                                    }""")
                    )
            ),
            @ApiResponse(responseCode = "403", description = "클럽장은 탈퇴할 수 없음.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽 탈퇴에 실패했습니다. 클럽장 권한을 변경하세요."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> leaveClub(@PathVariable UUID clubId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto);

    @Operation(summary = "클럽 삭제", description = "클럽을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽 삭제가 완료되었습니다."
                                    }""")
                    )),
            @ApiResponse(responseCode = "403", description = "클럽장만 삭제 가능.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽장만 클럽삭제가 가능합니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> deleteClub(@PathVariable UUID clubId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto);

    @Operation(summary = "클럽장 변경", description = "클럽장 권한을 다른 회원에게 이전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "403", description = "클럽장만 권한 이전 가능",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽장만 권한을 이전할 수 있습니다."
                                    }""")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "대상 회원 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽장 변경에 실패했습니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<PutClubLeaderResponse>> putClubLeader(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubLeaderRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    );

    // 그룹원 강퇴
    @Operation(summary = "클럽원 강퇴", description = "💡클럽에서 멤버를 강퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽원이 강퇴되었습니다."
                                    }""")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽원 강퇴에 실패했습니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> kickMemberFromClub(@PathVariable UUID clubId, @PathVariable UUID memberId);

    // 그룹원 목록 조회
    @Operation(summary = "클럽원 목록 조회", description = "💡클럽의 전체 멤버 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
            ),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "클럽원 목록 조회에 실패했습니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadClubMemberListResponse>> readClubMembers(@PathVariable UUID clubId);
}
