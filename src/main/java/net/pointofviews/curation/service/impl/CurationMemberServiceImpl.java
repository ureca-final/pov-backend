package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationMovieRedisService;
import net.pointofviews.curation.service.CurationMemberService;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationMemberServiceImpl implements CurationMemberService {

    private final CurationRepository curationRepository;
    private final CurationMovieRedisService curationMovieRedisService;
    private final MovieRepository movieRepository;

    @Override
    public ReadUserCurationListResponse readScheduledCurations(Pageable pageable) {
        // 모든 큐레이션 가져오기
        /**
         *         <- 스케줄러 처리된 큐레이션들 가져오기로 수정
         */
        List<Curation> curations = curationRepository.findAll(pageable).getContent();

        // 각 큐레이션의 영화 정보 가져오기
        List<ReadUserCurationResponse> userCurationResponses = curations.stream()
                .map(curation -> {
                    // Redis에서 영화 ID 가져오기
                    Set<Long> movieIds = curationMovieRedisService.readMoviesForCuration(curation.getId());

                    // JPA로 영화 정보 가져오기
                    Slice<ReadUserCurationMovieResponse> movieDetails = movieRepository.findUserCurationMoviesByIds(movieIds, pageable);

                    return new ReadUserCurationResponse(
                            curation.getTitle(),
                            movieDetails
                    );
                })
                .collect(Collectors.toList());

        return new ReadUserCurationListResponse(userCurationResponses);    }
}
