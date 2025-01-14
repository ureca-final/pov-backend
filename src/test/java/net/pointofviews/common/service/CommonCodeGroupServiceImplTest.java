package net.pointofviews.common.service;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.domain.CommonCodeGroup;
import net.pointofviews.common.exception.CommonCodeGroupException;
import net.pointofviews.common.repository.CommonCodeGroupRepository;
import net.pointofviews.common.service.impl.CommonCodeGroupServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class CommonCodeGroupServiceImplTest {

    @InjectMocks
    private CommonCodeGroupServiceImpl commonCodeGroupService;

    @Mock
    private CommonCodeGroupRepository commonCodeGroupRepository;

    @Nested
    class FindAll {

        @Nested
        class Success {

            @Test
            void 공통코드_그룹_전체조회() {
                // given
                List<CommonCodeGroup> codeGroup = CodeGroupEnum.getAllCodes().stream().toList();

                given(commonCodeGroupRepository.findAll()).willReturn(codeGroup);

                // when
                List<CommonCodeGroup> actual = commonCodeGroupService.findAll();

                // then
                Assertions.assertThat(actual).isEqualTo(codeGroup);
            }
        }

        @Nested
        class Failure {

            @Test
            void 공통코드_그룹_갯수_불일치_CommonCodeGroupException() {
                // given
                List<CommonCodeGroup> dbCodeGroup = spy(CodeGroupEnum.getAllCodes().stream().toList());
                int wrongSize = dbCodeGroup.size() + 1;

                given(commonCodeGroupRepository.findAll()).willReturn(dbCodeGroup);
                given(dbCodeGroup.size()).willReturn(wrongSize);

                // when & then
                Assertions.assertThatThrownBy(() -> commonCodeGroupService.findAll())
                        .isInstanceOf(CommonCodeGroupException.class)
                        .hasMessage(CommonCodeGroupException.commonCodeGroupSizeSyncError().getMessage());
            }

            @Test
            void 공통코드_enum_불일치_CommonCodeGroupException() {
                // given
                List<CommonCodeGroup> dbCodeGroup = spy(CodeGroupEnum.getAllCodes().stream().toList());
                CommonCodeGroup wrongCodeGroup = mock(CommonCodeGroup.class);

                given(commonCodeGroupRepository.findAll()).willReturn(dbCodeGroup);
                given(dbCodeGroup.get(0)).willReturn(wrongCodeGroup);
                // when & then
                Assertions.assertThatThrownBy(() -> commonCodeGroupService.findAll())
                        .isInstanceOf(CommonCodeGroupException.class)
                        .hasMessage(CommonCodeGroupException.enumAndDbCodeGroupMismatch(wrongCodeGroup).getMessage());
            }
        }
    }
}