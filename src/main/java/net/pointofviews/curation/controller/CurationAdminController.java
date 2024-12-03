package net.pointofviews.curation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.service.CurationAdminService;
import net.pointofviews.curation.service.CurationMovieCacheService;
import net.pointofviews.curation.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/movies/curations")
@RequiredArgsConstructor
public class CurationAdminController implements CurationAdminSpecification{

    private final CurationAdminService curationAdminService;

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<ReadCurationResponse>> createCuration(
            @Valid @RequestBody CreateCurationRequest createCurationRequest) {

        curationAdminService.saveCuration(createCurationRequest);
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
}
