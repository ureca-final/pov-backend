package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationRedisService;
import net.pointofviews.curation.service.CurationMemberService;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationMemberServiceImpl implements CurationMemberService {

    private final CurationRedisService curationRedisService;

    @Override
    public ReadUserCurationListResponse readUserCurations() {
        // 오늘 활성화된 큐레이션 ID 조회
        Set<Long> curationIds = curationRedisService.readTodayCurationId();

        // 각 큐레이션의 상세 정보 가져오기
        List<ReadUserCurationResponse> userCurationResponses = curationIds.stream()
                .map(curationId -> {
                    // Redis에서 큐레이션 상세 정보 조회
                    ReadUserCurationResponse curationDetail = curationRedisService.readTodayCurationDetail(curationId);

                    return curationDetail;
                })
                .collect(Collectors.toList());

        return new ReadUserCurationListResponse(userCurationResponses);
    }
}
