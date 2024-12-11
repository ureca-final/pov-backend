package net.pointofviews.curation.service;

import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import org.springframework.data.domain.Pageable;

public interface CurationMemberService {

    ReadUserCurationListResponse readScheduledCurations(Pageable pageable);

}
