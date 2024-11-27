package net.pointofviews.curation.service;

import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;

public interface CurationService {
    void saveCuration(CreateCurationRequest request);
    ReadCurationListResponse readAllCuration();
    ReadCurationResponse readCuration(Long curationId);
    void updateCuration(Long curationId, CreateCurationRequest request);
    void deleteCuration(Long curationId);
}
