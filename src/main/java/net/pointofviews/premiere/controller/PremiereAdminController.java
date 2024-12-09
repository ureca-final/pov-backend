package net.pointofviews.premiere.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.controller.specification.PremiereAdminSpecification;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import net.pointofviews.premiere.service.PremiereAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/admin/premieres")
public class PremiereAdminController implements PremiereAdminSpecification {

    private final PremiereAdminService premiereAdminService;

    @Override
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createPremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestBody @Valid PremiereRequest request
    ) {
        return null;
    }

    @Override
    @PutMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<Void>> putPremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId,
            @RequestBody @Valid PremiereRequest request
    ) {
        premiereAdminService.updatePremiere(loginMember, premiereId, request);

        return BaseResponse.ok("시사회 정보가 성공적으로 수정되었습니다.");
    }

    @Override
    @DeleteMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<Void>> deletePremiere(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId,
            @RequestBody @Valid PremiereRequest request
    ) {
        return null;
    }

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList(@AuthenticationPrincipal(expression = "member") Member loginMember) {
        return null;
    }

    @Override
    @GetMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetails(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId
    ) {
        return null;
    }

}
