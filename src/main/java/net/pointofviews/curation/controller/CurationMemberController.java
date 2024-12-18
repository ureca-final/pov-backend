package net.pointofviews.curation.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.controller.specification.CurationMemberSpecification;
import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.curation.service.CurationMemberService;
import net.pointofviews.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@RequestMapping("/api/movies/curations")
public class CurationMemberController implements CurationMemberSpecification {

    private final CurationMemberService curationMemberService;

    @GetMapping("/today")
    @Override
    public ResponseEntity<BaseResponse<ReadUserCurationListResponse>> readScheduledCurations(@AuthenticationPrincipal MemberDetailsDto memberDetails) {

        UUID memberId = memberDetails != null ? memberDetails.member().getId() : null;

        ReadUserCurationListResponse response = curationMemberService.readUserCurations(memberId);
        return BaseResponse.ok("사용자 스케줄링 된 큐레이션 조회에 성공하였습니다.", response);
    }
}
