package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.repository.ClubFavorGenreRepository;
import net.pointofviews.club.service.ClubFavorGenreService;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubFavorGenreServiceImpl implements ClubFavorGenreService {

    private final ClubFavorGenreRepository clubFavorGenreRepository;
    private final CommonCodeService commonCodeService;

    @Override
    public List<String> readGenreNamesByClubId(UUID clubId) {
        // 클럽의 장르 코드 조회
        List<String> genreCodes = clubFavorGenreRepository.findGenresByClubId(clubId);

        // 코드 -> 이름 변환
        return genreCodes.stream()
                .map(code -> commonCodeService.convertCommonCodeToName(code, CodeGroupEnum.MOVIE_GENRE))
                .collect(Collectors.toList());

    }
}
