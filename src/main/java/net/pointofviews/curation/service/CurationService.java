package net.pointofviews.curation.service;

import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;

public interface CurationService {
    ReadCurationListResponse readAllCurations();
    ReadCurationMoviesResponse readCuration(Long curationId);
}
