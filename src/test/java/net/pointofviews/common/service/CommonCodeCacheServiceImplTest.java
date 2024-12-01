package net.pointofviews.common.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCode;
import net.pointofviews.common.domain.CommonCodeGroup;
import net.pointofviews.common.domain.CommonCodeId;
import net.pointofviews.common.exception.CommonCodeException;
import net.pointofviews.common.repository.CommonCodeRepository;
import net.pointofviews.common.service.impl.CommonCodeCacheServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonCodeCacheServiceImplTest {

    @InjectMocks
    private CommonCodeCacheServiceImpl commonCodeServiceImpl;

    @Mock
    private CommonCodeRepository commonCodeRepository;

    @Nested
    class FindAll {

        @Nested
        class Success {

            @Test
            void 공통코드_전체조회() {
                // given
                CommonCodeGroup genreGroupCode = CommonCodeGroup.builder()
                        .codeGroupEnum(CodeGroupEnum.MOVIE_GENRE)
                        .build();

                List<CommonCode> commonCodes = Collections.singletonList(
                        CommonCode.builder()
                                .code("01")
                                .groupCode(genreGroupCode)
                                .description("액션")
                                .name("28")
                                .build()
                );
                given(commonCodeRepository.findAllByIsActiveTrue()).willReturn(commonCodes);

                // when
                List<CommonCode> result = commonCodeServiceImpl.findAll();

                // then
                Assertions.assertThat(result).isEqualTo(commonCodes);
                verify(commonCodeRepository, times(1)).findAllByIsActiveTrue();
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_그룹_코드를_가진_공통코드_CommonCodeException() {
                // given
                CommonCode wrongCommonCode = mock(CommonCode.class);
                CommonCodeId wrongCodeId = new CommonCodeId("00", "000");
                List<CommonCode> commonCodeList = Collections.singletonList(wrongCommonCode);

                given(commonCodeRepository.findAllByIsActiveTrue()).willReturn(commonCodeList);
                given(wrongCommonCode.getCode()).willReturn(wrongCodeId);

                // when & then
                Assertions.assertThatThrownBy(commonCodeServiceImpl::findAll)
                        .isInstanceOf(CommonCodeException.class)
                        .hasMessage(CommonCodeException.commonCodeIdError(wrongCodeId)
                                .getMessage());

            }

            @Test
            void 빈_공통코드_조회_CommonCodeException() {
                // given
                List<CommonCode> commonCodeList = new ArrayList<>();

                given(commonCodeRepository.findAllByIsActiveTrue()).willReturn(commonCodeList);

                // when & then
                Assertions.assertThatThrownBy(commonCodeServiceImpl::findAll)
                        .isInstanceOf(CommonCodeException.class)
                        .hasMessage(CommonCodeException.commonCodeEmptyError().getMessage());

            }
        }
    }
}
