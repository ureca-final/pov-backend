package net.pointofviews.curation.service;

import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.member.domain.Member;

public interface CurationAdminService {
    void saveCuration(Member member, CreateCurationRequest request);
    ReadCurationListResponse searchCurations(String theme, CurationCategory category);
    void updateCuration(Long curationId, CreateCurationRequest request);
    void deleteCuration(Long curationId);

    ReadCurationListResponse readAllCurations();
    ReadCurationMoviesResponse readCuration(Long curationId);
}
