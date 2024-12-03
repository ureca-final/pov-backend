package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.exception.CurationNotFoundException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationAdminService;
import net.pointofviews.curation.service.CurationMovieCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationAdminServiceImpl implements CurationAdminService {
    private final CurationRepository curationRepository;
    private final CurationMovieCacheService curationMovieCacheService;

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
        curationMovieCacheService.saveMoviesToCuration(savedCuration.getId(), request.movieIds());
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
                .orElseThrow(CurationNotFoundException::new);

        curation.updateCuration(
                request.theme(),
                request.category(),
                request.title(),
                request.description(),
                request.startTime()
        );

        // 영화 목록 캐싱 갱신
        curationMovieCacheService.saveMoviesToCuration(curationId, request.movieIds());
    }

    @Override
    @Transactional
    public void deleteCuration(Long curationId) {

        if(curationRepository.findById(curationId).isEmpty()){
            throw new CurationNotFoundException();
        };

        curationRepository.deleteById(curationId);
        curationMovieCacheService.deleteAllMoviesForCuration(curationId);
    }

}
