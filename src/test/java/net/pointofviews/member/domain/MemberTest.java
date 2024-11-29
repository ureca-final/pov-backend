package net.pointofviews.member.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

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
                SocialType socialType = SocialType.Google;
                RoleType roleType = RoleType.User;

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
                    softly.assertThat(member.isNoticeActive()).isTrue();
                    softly.assertThat(member.getCreatedAt()).isNull();
                    softly.assertThat(member.getDeletedAt()).isNull();
                });
            }
        }
    }
}
