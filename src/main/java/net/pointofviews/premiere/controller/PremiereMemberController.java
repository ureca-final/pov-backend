package net.pointofviews.premiere.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.premiere.controller.specification.PremiereMemberSpecification;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/premieres")
public class PremiereMemberController implements PremiereMemberSpecification {

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList() {
        return null;
    }

    @Override
    @GetMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetails(Long premiereId) {
        return null;
    }

    @Override
    @PostMapping("/{premiereId}/entry")
    public ResponseEntity<BaseResponse<Void>> createEntryPremiere(@PathVariable Long premiereId) {
        return null;
    }

}
