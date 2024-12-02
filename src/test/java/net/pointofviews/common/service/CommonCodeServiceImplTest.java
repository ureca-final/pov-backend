package net.pointofviews.common.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCode;
import net.pointofviews.common.domain.CommonCodeGroup;
import net.pointofviews.common.exception.CommonCodeException;
import net.pointofviews.common.service.impl.CommonCodeCacheServiceImpl;
import net.pointofviews.common.service.impl.CommonCodeServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CommonCodeServiceImplTest {

    @InjectMocks
    private CommonCodeServiceImpl commonCodeService;

    @Mock
    private CommonCodeCacheServiceImpl commonCodeCacheService;

    @Nested
    class ConvertGenreCodeToName {

        @Nested
        class Success {

            @Test
            void tmdb_장르_id_를_한글_장르로_변환() {
                // given
                String actionGenreId = "28";
                String genreName = "액션";

                CommonCodeGroup genreGroupCode = CommonCodeGroup.builder()
                        .codeGroupEnum(CodeGroupEnum.MOVIE_GENRE)
                        .build();

                CommonCode actionGenreCode = CommonCode.builder()
                        .code("01")
                        .name(actionGenreId)
                        .description(genreName)
                        .groupCode(genreGroupCode)
                        .build();
                List<CommonCode> codes = List.of(actionGenreCode);

                given(commonCodeCacheService.findAll()).willReturn(codes);

                // when
                String generatedCode = commonCodeService.convertCommonCodeNameToName(actionGenreId, CodeGroupEnum.MOVIE_GENRE);

                // then
                assertThat(generatedCode).isEqualTo(genreName);
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_장르_조회_CommonCodeException() {
                // given
                String invalidGenreId = "00";

                // when & then
                Assertions.assertThatThrownBy(
                                () -> commonCodeService.convertCommonCodeNameToName(invalidGenreId, CodeGroupEnum.MOVIE_GENRE)
                        ).isInstanceOf(CommonCodeException.class)
                        .hasMessage(CommonCodeException.commonCodeNotFound(invalidGenreId).getMessage());
            }
        }
    }

    @Nested
    class FindAllByCommonCodeGroup {

        @Nested
        class Success {

            @Test
            void 공통코드_그룹을_이용한_공통코드_조회() {
                // given
                CodeGroupEnum movieCodeEnum = CodeGroupEnum.MOVIE_GENRE;
                CommonCodeGroup movieGenreCode = CommonCodeGroup.builder().codeGroupEnum(CodeGroupEnum.MOVIE_GENRE).build();
                CommonCodeGroup tvGenreCode = CommonCodeGroup.builder().codeGroupEnum(CodeGroupEnum.TV_GENRE).build();
                CommonCode movieCode = mock(CommonCode.class);
                CommonCode tvCode = mock(CommonCode.class);

                List<CommonCode> commonCodeList = List.of(movieCode, tvCode);

                given(commonCodeCacheService.findAll()).willReturn(commonCodeList);
                given(movieCode.getGroupCode()).willReturn(movieGenreCode);
                given(tvCode.getGroupCode()).willReturn(tvGenreCode);

                // when
                List<CommonCode> actual = commonCodeService.findAllByCodeGroupEnum(movieCodeEnum);

                // then
                Assertions.assertThat(actual).containsExactly(movieCode);
            }
        }
    }
}