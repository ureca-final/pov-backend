package net.pointofviews.club.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.service.*;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubSearchServiceImpl implements ClubSearchService {

    private final ClubRepository clubRepository;
    private final CommonCodeService commonCodeService;


    @Override
    public SearchClubsListResponse searchClubs(String query, Pageable pageable) {

        Slice<SearchClubsResponse> responses = clubRepository.searchClubsByTitleOrNickname(query, pageable)
                .map(row -> {
                    UUID clubId = UUID.fromString((String) row[0]);
                    String clubName = (String) row[1];
                    String clubDescription = (String) row[2];
                    int participant = ((Number) row[3]).intValue();
                    int maxParticipant = ((Number) row[4]).intValue();
                    int clubMovieCount = ((Number) row[5]).intValue();

                    // 장르 코드를 이름으로 변환
                    String genreCodeString = (String) row[6];
                    List<String> genreNames = convertGenreCodesToNames(List.of(genreCodeString.split(",")));

                    return new SearchClubsResponse(
                            clubId,
                            clubName,
                            clubDescription,
                            participant,
                            maxParticipant,
                            clubMovieCount,
                            genreNames
                    );
                });

        return new SearchClubsListResponse(responses);
    }

    public List<String> convertGenreCodesToNames(List<String> genreCodes) {
        return genreCodes.stream()
                .map(code -> commonCodeService.convertCommonCodeToName(code, CodeGroupEnum.MOVIE_GENRE))
                .toList();
    }

}
