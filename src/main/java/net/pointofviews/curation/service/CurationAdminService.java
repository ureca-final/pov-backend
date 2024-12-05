package net.pointofviews.curation.service;

import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;

public interface CurationAdminService {
    void saveCuration(CreateCurationRequest request);
    ReadCurationListResponse searchCurations(String theme, CurationCategory category);
    void updateCuration(Long curationId, CreateCurationRequest request);
    void deleteCuration(Long curationId);
}
