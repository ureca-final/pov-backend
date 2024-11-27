package net.pointofviews.curation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies/curations")
@RequiredArgsConstructor
public class CurationController implements CurationSpecification{

    private final CurationService curationService;

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<Void>> createCuration(@RequestHeader("Authorization") String authorization,
                                                             @Valid @RequestBody CreateCurationRequest createCurationRequest) {
        curationService.saveCuration(createCurationRequest);
        return BaseResponse.ok("큐레이션이 성공적으로 생성되었습니다.");
    }

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadCurationListResponse>> readAllCurations() {
        return null;
    }

    @GetMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> readCuration(Long curationId) {
        return null;
    }

    @DeleteMapping("/{curationId}")
    @Override
    public ResponseEntity<Void> deleteCuration(Long curationId) {
        return null;
    }
}
