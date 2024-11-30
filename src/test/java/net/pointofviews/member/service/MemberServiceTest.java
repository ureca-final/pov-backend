package net.pointofviews.member.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.domain.RoleType;
import net.pointofviews.member.domain.SocialType;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void signup() {
    }

    @Nested
    class Login {
        @Nested
        class Success {
            @Test
            void 로그인_성공() {
                // given
                String email = "test@example.com";
                String socialType = "Google";

                Member member = mock(Member.class);
                given(member.getId()).willReturn(UUID.randomUUID());
                given(member.getEmail()).willReturn(email);
                given(member.getNickname()).willReturn("testuser");
                given(member.getRoleType()).willReturn(RoleType.User);
                given(member.getSocialType()).willReturn(SocialType.Google);

                given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

                LoginMemberRequest request = new LoginMemberRequest(email, socialType);

                // when
                LoginMemberResponse response = memberService.login(request);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response.email()).isEqualTo(email);
                    softly.assertThat(response.nickname()).isEqualTo("testuser");
                    softly.assertThat(response.role()).isEqualTo("User");
                    verify(memberRepository, times(1)).findByEmail(email);
                });
            }
        }

        @Nested
        class Failure {
            @Test
            void 존재하지_않는_이메일_MemberException_memberNotFound_예외발생() {
                // given
                String email = "nonexistent@example.com";
                given(memberRepository.findByEmail(email))
                        .willReturn(Optional.empty());

                LoginMemberRequest request = new LoginMemberRequest(
                        email, "GOOGLE"
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> memberService.login(request))
                            .isInstanceOf(MemberException.class)
                            .hasMessage("사용자를 찾을 수 없습니다.");
                });
            }

            @Test
            void 소셜타입_불일치_MemberException_invalidSocialType_예외발생() {
                // given
                String email = "test@example.com";
                Member member = mock(Member.class);
                given(member.getSocialType()).willReturn(SocialType.Google);
                given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

                LoginMemberRequest request = new LoginMemberRequest(
                        email, "NAVER"  // 다른 소셜 타입으로 요청
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> memberService.login(request))
                            .isInstanceOf(MemberException.class)
                            .hasMessage("잘못된 소셜 로그인 타입입니다.");
                });
            }
        }
    }

    @Test
    void deleteMember() {
    }

    @Test
    void updateGenre() {
    }

    @Test
    void updateImage() {
    }

    @Test
    void updateNickname() {
    }

    @Test
    void updateNotice() {
    }
}