package net.pointofviews.premiere.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.premiere.controller.specification.PremiereMemberSpecification;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import net.pointofviews.premiere.service.PremiereMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@RequestMapping("/api/premieres")
public class PremiereMemberController implements PremiereMemberSpecification {

    private final PremiereMemberService premiereMemberService;

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList() {
        ReadPremiereListResponse response = premiereMemberService.findAllPremiere();

        if (response.premieres().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("모든 시사회가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @GetMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetail(@PathVariable Long premiereId) {
        ReadDetailPremiereResponse response = premiereMemberService.findPremiereDetail(premiereId);

        return BaseResponse.ok("시사회 상제 정보가 성공적으로 조회되었습니다.", response);
    }
}
