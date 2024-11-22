package net.pointofviews.curation.domain;

import static net.pointofviews.curation.domain.CurationCategory.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import net.pointofviews.member.domain.Member;

@ExtendWith(value = MockitoExtension.class)
class CurationTest {

	@Nested
	class Constructor {

		@Nested
		class success {

			@Test
			void Curation_객체_생성() {
			    // given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				String title = "강동원의 영화 모아보기";
				String theme = "영화배우";
				String description = "강동원의 모든 영화";
				CurationCategory category = ACTOR;

			    // when -- 테스트하고자 하는 행동
				Curation curation = Curation.builder()
					.member(member)
					.title(title)
					.theme(theme)
					.description(description)
					.category(category)
					.build();

			    // then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(curation.getMember()).isNotNull();
					softly.assertThat(curation.getTitle()).isEqualTo(title);
					softly.assertThat(curation.getTheme()).isEqualTo(theme);
					softly.assertThat(curation.getDescription()).isEqualTo(description);
					softly.assertThat(curation.getCategory()).isEqualTo(category);
					softly.assertThat(curation.getCreatedAt()).isNull();
				});
			}
		}
	}
}