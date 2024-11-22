package net.pointofviews.common.domain;

import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonCodeGroupTest {

	@Nested
	class Constructor {

		@Nested
		class Success {

			@Test
			void CommonCodeGroup_객체_생성() {
			    // given -- 테스트의 상태 설정
				String groupCode = "010";
				String name = "장르";
				String description = "영화 장르";

			    // when -- 테스트하고자 하는 행동
				CommonCodeGroup commonCodeGroup = CommonCodeGroup.builder()
					.groupCode(groupCode)
					.name(name)
					.description(description)
					.build();

			    // then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(commonCodeGroup.getGroupCode()).isEqualTo(groupCode);
					softly.assertThat(commonCodeGroup.getName()).isEqualTo(name);
					softly.assertThat(commonCodeGroup.getDescription()).isEqualTo(description);
				});
			}
		}
	}
}