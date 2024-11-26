package net.pointofviews.curation.controller;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies/curations")
public class CurationController implements CurationSpecification{

    @PostMapping
    @Override
    public ResponseEntity<BaseResponse<Void>> createCuration(String authorization, CreateCurationRequest createCurationRequest) {
        return null;
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
