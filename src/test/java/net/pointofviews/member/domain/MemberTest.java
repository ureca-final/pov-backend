package net.pointofviews.member.domain;

import static net.pointofviews.member.domain.RoleType.*;
import static net.pointofviews.member.domain.SocialType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberTest {

	@Nested
	class Constructor {

		@Nested
		class Success {

			@Test
			void Member_객체_생성() {
				// given
				String email = "email@email.com";
				String profileImage = "profileImage";
				LocalDate birth = LocalDate.of(1990, 1, 1);
				String nickname = "nickname";
				SocialType socialType = GOOGLE;
				RoleType roleType = USER;

				// when
				Member member = Member.builder()
					.email(email)
					.birth(birth)
					.nickname(nickname)
					.socialType(socialType)
					.roleType(roleType)
					.profileImage(profileImage)
					.build();

				// then
				SoftAssertions.assertSoftly(softly -> {
					softly.assertThat(member).isNotNull();
					softly.assertThat(member.getId()).isNull();
					softly.assertThat(member.getEmail()).isEqualTo(email);
					softly.assertThat(member.getBirth()).isEqualTo(birth);
					softly.assertThat(member.getNickname()).isEqualTo(nickname);
					softly.assertThat(member.getSocialType()).isEqualTo(socialType);
					softly.assertThat(member.getRoleType()).isEqualTo(roleType);
					softly.assertThat(member.getProfileImage()).isEqualTo(profileImage);
					softly.assertThat(member.isNoticeActive()).isFalse();
					softly.assertThat(member.getCreatedAt()).isNull();
					softly.assertThat(member.getDeletedAt()).isNull();
				});
			}
		}
	}

	@Nested
	class UpdateProfileImage {

		@Nested
		class Success {

			@Test
			void 프로필_이미지_변경() {
				// given -- 테스트의 상태 설정
				String profileImage = "https://s3-bucket.../profileImage.jpg";
				Member member = Member.builder()
					.email("email@eail.com")
					.birth(LocalDate.of(1990, 1, 1))
					.nickname("nickname")
					.socialType(GOOGLE)
					.roleType(USER)
					.profileImage(profileImage)
					.build();

				// when -- 테스트하고자 하는 행동
				member.updateProfileImage("https://s3-bucket.../newProfileImage.jpg");

				// then -- 예상되는 변화 및 결과
				assertThat(member.getProfileImage()).isEqualTo("https://s3-bucket.../newProfileImage.jpg");
			}
		}
	}
}
