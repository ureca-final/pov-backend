package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.ReadCurationListResponse;
import net.pointofviews.curation.dto.response.ReadCurationResponse;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CurationServiceImpl implements CurationService {
    private final CurationRepository curationRepository;

    @Override
    public void saveCuration(CreateCurationRequest createCurationRequest) {
        // 시작 시간을 LocalDateTime으로 변환
        LocalDateTime startTime = LocalDateTime.parse(createCurationRequest.startTime(), DateTimeFormatter.ISO_DATE_TIME);

        // Curation 엔티티 생성
        Curation curation = Curation.builder()
                .theme(createCurationRequest.theme())
                .category(createCurationRequest.category())
                .title(createCurationRequest.title())
                .description(createCurationRequest.description())
                .build();

        // 엔티티 저장
        curationRepository.save(curation);
    }


    @Override
    public ReadCurationListResponse readAllCuration() {
        return null;
    }

    @Override
    public ReadCurationResponse readCuration(Long curationId) {
        return null;
    }

    @Override
    public void updateCuration(Long curationId, CreateCurationRequest request) {

    }

    @Override
    public void deleteCuration(Long curationId) {

    }
}
