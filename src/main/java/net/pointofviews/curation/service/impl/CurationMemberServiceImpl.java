package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.dto.response.ReadUserCurationListResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.dto.response.ReadUserCurationResponse;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationRedisService;
import net.pointofviews.curation.service.CurationMemberService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationMemberServiceImpl implements CurationMemberService {

    private final CurationRedisService curationRedisService;
    private final CurationRepository curationRepository;
    private final MovieRepository movieRepository;

    @Override
    public ReadUserCurationListResponse readUserCurations(UUID memberId) {

        // 오늘 활성화된 큐레이션 ID 조회
        Set<Long> curationIds = curationRedisService.readTodayCurationId();

        // 각 큐레이션 별 Movie 정보 가져오기
        List<ReadUserCurationResponse> userCurationResponses = curationIds.stream()
                .map(curationId -> {

                    // 큐레이션 title
                    String curationTitle = curationRepository.findTitleById(curationId);

                    // Redis에서 큐레이션 별 영화 ID 조회
                    Set<Long> movieIds = curationRedisService.readMoviesForCuration(curationId);

                    // 영화 정보를 DB에서 조회
                    List<ReadUserCurationMovieResponse> movieDetails = movieRepository.findUserCurationMoviesByIds(movieIds, memberId)
                            .stream()
                            .map(movie -> new ReadUserCurationMovieResponse(
                                    movie.title(),
                                    movie.poster(),
                                    movie.released(),
                                    movie.isLiked(),
                                    movie.movieLikeCount(),
                                    movie.movieReviewCount()
                            ))
                            .collect(Collectors.toList());

                    return new ReadUserCurationResponse(curationTitle ,movieDetails);
                })
                .collect(Collectors.toList());

        return new ReadUserCurationListResponse(userCurationResponses);
    }
}
