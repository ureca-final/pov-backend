package net.pointofviews.club.controller;

import jakarta.validation.Valid;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.club.dto.request.*;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clubs")
public class ClubController implements ClubSpecification{

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllClubs() {
        return null;
    }

    @GetMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<ReadClubDetailsResponse>> readClubDetails(String clubId) {
        return null;
    }

    @GetMapping("/myclub")
    @Override
    public ResponseEntity<BaseResponse<ReadMyClubsListResponse>> readMyClubs() {
        return null;
    }

    // 그룹 생성
    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<CreateClubResponse>> createClub(@Valid @RequestBody CreateClubRequest request) {
        return null;
    }

    // 그룹 수정
    @PutMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<PutClubResponse>> putClub(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubRequest request) {
        return null;
    }

    // 그룹 탈퇴
    @DeleteMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> deleteClub(@PathVariable UUID clubId) {
        return null;
    }

    // 그룹장 변경
    @PutMapping("/{clubId}/leader")
    @Override
    public ResponseEntity<BaseResponse<PutClubLeaderResponse>> putClubLeader(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubLeaderRequest request) {
        return null;
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
}
