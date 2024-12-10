package net.pointofviews.curation.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.controller.specification.CurationSpecification;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/curations")
@RequiredArgsConstructor
public class CurationController implements CurationSpecification {

    private final CurationService curationService;

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadCurationListResponse>> readAllCurations() {
        ReadCurationListResponse response = curationService.readAllCurations();
        return BaseResponse.ok("큐레이션 전체 조회에 성공하였습니다.", response);
    }

    @GetMapping("/{curationId}")
    @Override
    public ResponseEntity<BaseResponse<ReadCurationMoviesResponse>> readCuration(@PathVariable Long curationId) {
        ReadCurationMoviesResponse response = curationService.readCuration(curationId);
        return BaseResponse.ok("큐레이션 조회에 성공하였습니다.", response);
    }

}
