package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationAdminService;
import net.pointofviews.curation.service.CurationMovieRedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationAdminServiceImpl implements CurationAdminService {
    private final CurationRepository curationRepository;
    private final CurationMovieRedisService curationMovieRedisService;

    @Override
    @Transactional
    public void saveCuration(CreateCurationRequest request) {

        Curation curation = Curation.builder()
                .theme(request.theme())
                .category(request.category())
                .title(request.title())
                .description(request.description())
                .startTime(request.startTime())
                .build();

        Curation savedCuration = curationRepository.save(curation);

        // 캐싱 영화 ID 저장
        curationMovieRedisService.saveMoviesToCuration(savedCuration.getId(), request.movieIds());
    }

    @Override
    public ReadCurationListResponse searchCurations(String theme, CurationCategory category) {
        List<Curation> searchedCurations = curationRepository.searchCurations(theme, category);

        // 검색 결과를 Response로 변환
        List<ReadCurationResponse> responses = searchedCurations.stream()
                .map(curation -> new ReadCurationResponse(
                        curation.getId(),
                        curation.getTheme(),
                        curation.getCategory(),
                        curation.getTitle(),
                        curation.getDescription(),
                        curation.getStartTime()
                ))
                .collect(Collectors.toList());

        return new ReadCurationListResponse(responses);

    }

    @Override
    @Transactional
    public void updateCuration(Long curationId, CreateCurationRequest request) {
        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(CurationException::CurationNotFound);

        curation.updateCuration(
                request.theme(),
                request.category(),
                request.title(),

                request.description(),
                request.startTime()
        );

        // 영화 목록 캐싱 갱신
        curationMovieRedisService.updateMoviesToCuration(curationId, request.movieIds());
    }

    @Override
    @Transactional
    public void deleteCuration(Long curationId) {

        if(!curationRepository.existsById(curationId)) {
            throw CurationException.CurationNotFound();
        };

        curationRepository.deleteById(curationId);
        curationMovieRedisService.deleteAllMoviesForCuration(curationId);
    }


    @Override
    public ReadCurationListResponse readAllCurations() {
        List<ReadCurationResponse> curationResponses = curationRepository.findAll()
                .stream()
                .map(curation -> new ReadCurationResponse(
                        curation.getId(),
                        curation.getTheme(),
                        curation.getCategory(),
                        curation.getTitle(),
                        curation.getDescription(),
                        curation.getStartTime()
                ))
                .collect(Collectors.toList());

        return new ReadCurationListResponse(curationResponses);
    }

    @Override
    public ReadCurationMoviesResponse readCuration(Long curationId) {
        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(CurationException::CurationNotFound);

        Set<Long> movieIds = curationMovieRedisService.readMoviesForCuration(curationId);

        ReadCurationResponse curationResponse = new ReadCurationResponse(
                curation.getId(),
                curation.getTheme(),
                curation.getCategory(),
                curation.getTitle(),
                curation.getDescription(),
                curation.getStartTime()
        );

        return new ReadCurationMoviesResponse(curationResponse, movieIds);
    }
}
