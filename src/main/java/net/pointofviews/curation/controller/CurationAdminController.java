package net.pointofviews.curation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.controller.specification.CurationAdminSpecification;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadAdminAllCurationListResponse;
import net.pointofviews.curation.dto.response.ReadAdminCurationDetailResponse;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.service.CurationAdminService;
import net.pointofviews.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/movies/curations")
public class CurationAdminController implements CurationAdminSpecification {

    private final CurationAdminService curationAdminService;

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> createCuration(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @Valid @RequestBody CreateCurationRequest createCurationRequest) {

        curationAdminService.saveCuration(loginMember, createCurationRequest);
        return BaseResponse.ok("큐레이션이 성공적으로 생성되었습니다.");
    }

    @GetMapping("/search")
    @Override
    public ResponseEntity<BaseResponse<ReadCurationListResponse>> searchCurations(
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) CurationCategory category) {

        ReadCurationListResponse response = curationAdminService.searchCurations(theme, category);
        return BaseResponse.ok("큐레이션 검색 성공", response);
    }

    @PutMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> updateCuration(
            @PathVariable Long curationId,
            @Valid @RequestBody CreateCurationRequest createCurationRequest) {

        curationAdminService.updateCuration(curationId, createCurationRequest);
        return BaseResponse.ok("큐레이션이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<Void>> deleteCuration(
            @PathVariable Long curationId) {
        curationAdminService.deleteCuration(curationId);
        return BaseResponse.ok("큐레이션이 성공적으로 삭제되었습니다.");
    }

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadAdminAllCurationListResponse>> readAllCurations() {
        ReadAdminAllCurationListResponse response = curationAdminService.readAllCurations();
        return BaseResponse.ok("관리자용 큐레이션 전체 조회에 성공하였습니다.", response);
    }

    @GetMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<ReadAdminCurationDetailResponse>> readCurationDetail(@PathVariable Long curationId) {
        ReadAdminCurationDetailResponse response = curationAdminService.readCurationDetail(curationId);
        return BaseResponse.ok("관리자용 큐레이션 상세 조회에 성공하였습니다.", response);
    }

}
