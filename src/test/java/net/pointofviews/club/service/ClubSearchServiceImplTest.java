package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.SearchClubsListResponse;
import net.pointofviews.club.dto.response.SearchClubsResponse;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.service.impl.ClubSearchServiceImpl;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ClubSearchServiceImplTest {

    @InjectMocks
    private ClubSearchServiceImpl clubSearchService;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private CommonCodeService commonCodeService;

    @Nested
    class SearchClubs {

        @Test
        void 클럽_검색_성공() {
            // given
            String query = "test";
            Pageable pageable = PageRequest.of(0, 10);

            // Mocking 데이터 생성
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{
                    UUID.randomUUID().toString(), // clubId
                    "Club Name",                 // clubName
                    "Club Description",          // clubDescription
                    5,                           // participant
                    10,                          // maxParticipant
                    3,                           // clubMovieCount
                    "GENRE1,GENRE2"             // genreCodes
            });


            Slice<Object[]> mockSlice = new PageImpl<>(mockResults, pageable, mockResults.size());
            given(clubRepository.searchClubsByTitleOrNickname(query, pageable)).willReturn(mockSlice);

            given(commonCodeService.convertCommonCodeToName("GENRE1", CodeGroupEnum.MOVIE_GENRE)).willReturn("Action");
            given(commonCodeService.convertCommonCodeToName("GENRE2", CodeGroupEnum.MOVIE_GENRE)).willReturn("Drama");

            // when
            SearchClubsListResponse response = clubSearchService.searchClubs(query, pageable);

            // then
            assertThat(response.clubs().getContent()).hasSize(1);
            SearchClubsResponse club = response.clubs().getContent().get(0);
            assertThat(club.clubName()).isEqualTo("Club Name");
            assertThat(club.clubFavorGenres()).containsExactly("Action", "Drama");
        }

        @Test
        void 클럽_검색_결과없음() {
            // given
            String query = "nonexistent";
            Pageable pageable = PageRequest.of(0, 10);

            Slice<Object[]> mockSlice = new PageImpl<>(List.of(), pageable, 0);
            given(clubRepository.searchClubsByTitleOrNickname(query, pageable)).willReturn(mockSlice);

            // when
            SearchClubsListResponse response = clubSearchService.searchClubs(query, pageable);

            // then
            assertThat(response.clubs().getContent()).isEmpty();
        }

        @Test
        void 클럽_검색_장르코드_변환_실패() {
            // given
            String query = "test";
            Pageable pageable = PageRequest.of(0, 10);

            // Mocking 데이터 생성
            List<Object[]> mockResults = new ArrayList<>();
            mockResults.add(new Object[]{
                    UUID.randomUUID().toString(),
                    "Club Name",
                    "Club Description",
                    5,
                    10,
                    3,
                    "INVALID_GENRE"
            });

            Slice<Object[]> mockSlice = new PageImpl<>(mockResults, pageable, mockResults.size());
            given(clubRepository.searchClubsByTitleOrNickname(query, pageable)).willReturn(mockSlice);

            // 정확한 매개변수 전달
            given(commonCodeService.convertCommonCodeToName("INVALID_GENRE", CodeGroupEnum.MOVIE_GENRE))
                    .willThrow(new RuntimeException("Invalid genre code"));

            // when & then
            assertThatThrownBy(() -> clubSearchService.searchClubs(query, pageable))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid genre code");
        }
    }
}