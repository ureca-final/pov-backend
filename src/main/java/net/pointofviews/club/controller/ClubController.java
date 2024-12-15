package net.pointofviews.club.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.club.controller.specification.ClubSpecification;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.request.PutClubLeaderRequest;
import net.pointofviews.club.dto.request.PutClubRequest;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.club.service.ClubSearchService;
import net.pointofviews.club.service.ClubService;
import net.pointofviews.club.service.MemberClubService;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController implements ClubSpecification {

    private final ClubService clubService;
    private final ClubSearchService clubSearchService;
    private final MemberClubService memberClubService;

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllClubs() {
        ReadAllClubsListResponse response = clubService.readAllPublicClubs();
        return BaseResponse.ok("공개된 모든 클럽이 성공적으로 조회되었습니다.", response);
    }

    @GetMapping("/search")
    @Override
    public ResponseEntity<BaseResponse<SearchClubsListResponse>> searchClubs(@RequestParam String query,
                                                                             Pageable pageable) {
        SearchClubsListResponse response = clubSearchService.searchClubs(query, pageable);
        return BaseResponse.ok("공개된 모든 클럽이 성공적으로 검색되었습니다.", response);
    }

    @GetMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<ReadClubDetailsResponse>> readClubDetails(@PathVariable UUID clubId, @AuthenticationPrincipal(expression = "member") Member loginMember, Pageable pageable) {
        ReadClubDetailsResponse response = clubService.readClubDetails(clubId, loginMember, pageable);
        return BaseResponse.ok("클럽 상세 정보를 성공적으로 조회했습니다.", response);
    }

    @GetMapping("/myclub")
    @Override
    public ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllMyClubs(@AuthenticationPrincipal(expression = "member") Member loginMember) {
        ReadAllClubsListResponse response = clubService.readAllMyClubs(loginMember);
        return BaseResponse.ok("사용자가 속한 모든 클럽이 성공적으로 조회되었습니다.", response);
    }

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<CreateClubResponse>> createClub(@Valid @RequestBody CreateClubRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        CreateClubResponse response = clubService.saveClub(request, memberDetailsDto.member());
        return BaseResponse.ok("클럽이 성공적으로 생성되었습니다.", response);
    }

    @Override
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<CreateClubImageResponse>> createClubImages(
            @RequestPart(value = "file") MultipartFile file,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        CreateClubImageResponse response = clubService.saveClubImages(file, memberDetailsDto.member());
        return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", response);
    }

    @Override
    @PostMapping(value = "/{clubId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<CreateClubImageResponse>> putClubImages(
            @PathVariable UUID clubId,
            @RequestPart(value = "file") MultipartFile file,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        CreateClubImageResponse response = clubService.updateClubImages(clubId, file, memberDetailsDto.member());
        return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", response);
    }

    @PutMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<PutClubResponse>> putClub(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        PutClubResponse response = clubService.updateClub(clubId, request, memberDetailsDto.member());
        return BaseResponse.ok("클럽이 성공적으로 수정되었습니다.", response);
    }

    @DeleteMapping("/{clubId}/leave")
    @Override
    public ResponseEntity<BaseResponse<Void>> leaveClub(@PathVariable UUID clubId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        clubService.leaveClub(clubId, memberDetailsDto.member());
        return BaseResponse.ok("클럽을 성공적으로 탈퇴하였습니다.");
    }

    @DeleteMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> deleteClub(@PathVariable UUID clubId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        clubService.deleteClub(clubId, memberDetailsDto.member());
        return BaseResponse.ok("클럽이 성공적으로 삭제되었습니다.");
    }

    @PutMapping("/{clubId}/leader")
    @Override
    public ResponseEntity<BaseResponse<PutClubLeaderResponse>> putClubLeader(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubLeaderRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        PutClubLeaderResponse response = clubService.updateClubLeader(clubId, request, memberDetailsDto.member());
        return BaseResponse.ok("클럽장이 성공적으로 변경되었습니다.", response);
    }

    // 그룹원 강퇴
    @PutMapping("/{clubId}/member/{memberId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> kickMemberFromClub(
            @PathVariable UUID clubId,
            @PathVariable UUID memberId) {
        return null;
    }

    // 그룹원 목록 조회
    @GetMapping("/{clubId}/member")
    @Override
    public ResponseEntity<BaseResponse<ReadClubMemberListResponse>> readClubMembers(@PathVariable UUID clubId) {
        return null;
    }

    @Override
    @PostMapping("/{clubId}/member")
    public ResponseEntity<BaseResponse<Void>> joinClub(@PathVariable UUID clubId, @AuthenticationPrincipal(expression = "member") Member loginMember) {
        memberClubService.joinClub(clubId, loginMember);
        return BaseResponse.ok("클럽 가입에 성공했습니다");
    }

    @Override
    @PostMapping("/{clubId}/invite-code")
    public ResponseEntity<BaseResponse<String>> generateInviteCode(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal(expression = "member")
            Member loginMember) {
        String code = memberClubService.generateInviteCode(clubId, loginMember);
        return BaseResponse.ok("초대코드 생성에 성공했습니다.", code);
    }
}
