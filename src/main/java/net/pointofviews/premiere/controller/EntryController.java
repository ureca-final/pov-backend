package net.pointofviews.premiere.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.controller.specification.EntrySpecification;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.request.DeleteEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.dto.response.ReadMyEntryListResponse;
import net.pointofviews.premiere.service.EntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/premieres")
public class EntryController implements EntrySpecification {

    private final EntryService entryService;

    @Override
    @PostMapping("/{premiereId}/entry")
    public ResponseEntity<BaseResponse<CreateEntryResponse>> createEntry(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId,
            @RequestBody @Valid CreateEntryRequest request
    ) {
        CreateEntryResponse response = entryService.saveEntry(loginMember, premiereId, request);

        return BaseResponse.ok("시사회 응모가 성공적으로 완료되었습니다.", response);
    }

    @Override
    @DeleteMapping("/{premiereId}/entry/cancel")
    public ResponseEntity<BaseResponse<Void>> cancelEntry(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long premiereId,
            @RequestBody @Valid DeleteEntryRequest request
    ) {
        entryService.deleteEntry(loginMember, premiereId, request);

        return BaseResponse.ok("시사회 응모가 성공적으로 취소되었습니다.");
    }

    @Override
    @GetMapping("/entry/my")
    public ResponseEntity<BaseResponse<ReadMyEntryListResponse>> readMyEntry(@AuthenticationPrincipal(expression = "member") Member loginMember) {
        ReadMyEntryListResponse response = entryService.findMyEntryList(loginMember);

        if (response.entry().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("내 티켓팅 내역이 성공적으로 조회되었습니다.", response);
    }
}
