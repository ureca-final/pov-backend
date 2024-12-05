package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationMoviesResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationMovieRedisService;
import net.pointofviews.curation.service.CurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationServiceImpl implements CurationService {

    private final CurationRepository curationRepository;
    private final CurationMovieRedisService curationMovieRedisService;

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
