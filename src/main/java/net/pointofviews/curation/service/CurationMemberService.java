package net.pointofviews.curation.service;

import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CurationMemberService {
    ReadUserCurationListResponse readUserCurations(UUID memberId);
}
