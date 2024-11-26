package net.pointofviews.premiere.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.premiere.dto.request.CreatePremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premieres")
public class PremiereController implements PremiereSpecification {
    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<ReadPremiereListResponse>> readPremiereList() {
        return null;
    }

    @Override
    @GetMapping("/{premiereId}")
    public ResponseEntity<BaseResponse<ReadDetailPremiereResponse>> readPremiereDetails(@PathVariable Long premiereId) {
        return null;
    }

    @Override
    @PostMapping("/{premiereId}/entry")
    public ResponseEntity<BaseResponse<Void>> createEntryPremiere(@PathVariable Long premiereId) {
        return null;
    }

    @Override
    @PostMapping
    public ResponseEntity<BaseResponse<CreatePremiereRequest>> createPremiere(@RequestBody CreatePremiereRequest premiere) {
        return null;
    }

}
