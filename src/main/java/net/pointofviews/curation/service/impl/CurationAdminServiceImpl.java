package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.curation.dto.request.CreateCurationRequest;
import net.pointofviews.curation.dto.response.*;
import net.pointofviews.curation.exception.CurationException;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationAdminService;
import net.pointofviews.curation.service.CurationRedisService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.pointofviews.curation.exception.CurationException.CurationIdNotFound;
import static net.pointofviews.member.exception.MemberException.adminNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationAdminServiceImpl implements CurationAdminService {
    private final CurationRepository curationRepository;
    private final MemberRepository memberRepository;
    private final CurationRedisService curationRedisService;

    @Override
    @Transactional
    public void saveCuration(Member member, CreateCurationRequest request) {

        if (memberRepository.findById(member.getId()).isEmpty()) {
            throw adminNotFound(member.getId());
        }

        Curation curation = Curation.builder()
                .member(member)
                .theme(request.theme())
                .category(request.category())
                .title(request.title())
                .description(request.description())
                .startTime(request.startTime())
                .build();

        Curation savedCuration = curationRepository.save(curation);

        // 캐싱 영화 ID 저장
        curationRedisService.saveMoviesToCuration(savedCuration.getId(), request.movieIds());
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
        curationRedisService.updateMoviesToCuration(curationId, request.movieIds());
    }

    @Override
    @Transactional
    public void deleteCuration(Long curationId) {

        if(!curationRepository.existsById(curationId)) {
            throw CurationException.CurationNotFound();
        };

        curationRepository.deleteById(curationId);
        curationRedisService.deleteAllMoviesForCuration(curationId);
    }


    @Override
    public ReadAdminAllCurationListResponse readAllCurations() {
        List<ReadAdminAllCurationResponse> responses = curationRepository.findAllCurations();
        return new ReadAdminAllCurationListResponse(responses);
    }

    @Override
    public ReadAdminCurationDetailResponse readCurationDetail(Long curationId) {
        // 큐레이션 상세 정보 가져오기
        ReadAdminCurationResponse curation = curationRepository.findCurationDetailById(curationId)
                .orElseThrow(() -> CurationIdNotFound(curationId));

        // Redis에서 영화 ID 가져오기
        Set<Long> movieIds = curationRedisService.readMoviesForCuration(curationId);

        // 영화 정보 가져오기
        List<ReadAdminCurationMovieResponse> movies = movieIds.isEmpty()
                ? Collections.emptyList()
                : curationRepository.findMoviesByIds(movieIds);

        return new ReadAdminCurationDetailResponse(curation, movies);
    }
}
