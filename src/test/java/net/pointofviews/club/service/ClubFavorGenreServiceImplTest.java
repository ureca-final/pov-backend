package net.pointofviews.club.service;

import net.pointofviews.club.repository.ClubFavorGenreRepository;
import net.pointofviews.club.service.impl.ClubFavorGenreServiceImpl;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubFavorGenreServiceImplTest {

    @InjectMocks
    private ClubFavorGenreServiceImpl clubFavorGenreService;

    @Mock
    private ClubFavorGenreRepository clubFavorGenreRepository;

    @Mock
    private CommonCodeService commonCodeService;

    @Nested
    class ReadGenreNamesByClubId {

        @Test
        void 클럽_장르명_조회_성공() {
            // given
            UUID clubId = UUID.randomUUID();
            List<String> genreCodes = List.of("001", "002");
            List<String> genreNames = List.of("Action", "Romance");

            given(clubFavorGenreRepository.findGenresByClubId(clubId)).willReturn(genreCodes);
            given(commonCodeService.convertCommonCodeToName("001", CodeGroupEnum.MOVIE_GENRE)).willReturn("Action");
            given(commonCodeService.convertCommonCodeToName("002", CodeGroupEnum.MOVIE_GENRE)).willReturn("Romance");

            // when
            List<String> result = clubFavorGenreService.readGenreNamesByClubId(clubId);

            // then
            assertThat(result).containsExactly("Action", "Romance");
            verify(clubFavorGenreRepository).findGenresByClubId(clubId);
            verify(commonCodeService).convertCommonCodeToName("001", CodeGroupEnum.MOVIE_GENRE);
            verify(commonCodeService).convertCommonCodeToName("002", CodeGroupEnum.MOVIE_GENRE);
        }

        @Test
        void 클럽_장르명_조회_성공_빈_리스트() {
            // given
            UUID clubId = UUID.randomUUID();
            List<String> genreCodes = List.of();

            given(clubFavorGenreRepository.findGenresByClubId(clubId)).willReturn(genreCodes);

            // when
            List<String> result = clubFavorGenreService.readGenreNamesByClubId(clubId);

            // then
            assertThat(result).isEmpty();
            verify(clubFavorGenreRepository).findGenresByClubId(clubId);
        }

        @Test
        void 장르_코드에_해당하는_이름_찾을_수_없음() {
            // given
            UUID clubId = UUID.randomUUID();
            List<String> genreCodes = List.of("001", "999"); // 999는 없는 코드

            given(clubFavorGenreRepository.findGenresByClubId(clubId)).willReturn(genreCodes);
            given(commonCodeService.convertCommonCodeToName("001", CodeGroupEnum.MOVIE_GENRE)).willReturn("Action");
            given(commonCodeService.convertCommonCodeToName("999", CodeGroupEnum.MOVIE_GENRE))
                    .willThrow(new RuntimeException("코드 999에 해당하는 장르명을 찾을 수 없습니다."));

            // when & then
            assertThatThrownBy(() -> clubFavorGenreService.readGenreNamesByClubId(clubId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("코드 999에 해당하는 장르명을 찾을 수 없습니다.");

            verify(clubFavorGenreRepository).findGenresByClubId(clubId);
            verify(commonCodeService).convertCommonCodeToName("001", CodeGroupEnum.MOVIE_GENRE);
            verify(commonCodeService).convertCommonCodeToName("999", CodeGroupEnum.MOVIE_GENRE);
        }
    }
}