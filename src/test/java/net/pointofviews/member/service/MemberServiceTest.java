package net.pointofviews.member.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.common.exception.CommonCodeException;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.domain.MemberFavorGenre;
import net.pointofviews.member.domain.RoleType;
import net.pointofviews.member.domain.SocialType;
import net.pointofviews.member.dto.request.PutMemberNicknameRequest;
import net.pointofviews.member.dto.response.PutMemberNicknameResponse;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberFavorGenreRepository;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.impl.MemberServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
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

	@Mock
	private MemberFavorGenreRepository memberFavorGenreRepository;

	@Mock
	private CommonCodeService commonCodeService;

	@Test
	void signup() {
	}
    @Nested
    class Signup {
        @Nested
        class Success {
			@Test
			void 회원가입_성공_장르선택있음() {
				// given
				String email = "test@example.com";
				String nickname = "testuser";
				LocalDate birth = LocalDate.of(2000, 1, 1);
				String socialType = "NAVER";
				String profileImage = "https://example.com/image.jpg";
				List<String> genres = List.of("로맨스", "코미디", "액션");

				CreateMemberRequest request = new CreateMemberRequest(
						email,
						nickname,
						birth,
						socialType,
						genres,
						profileImage
				);

				UUID memberId = UUID.randomUUID();
				Member savedMember = mock(Member.class);
				given(savedMember.getId()).willReturn(memberId);
				given(savedMember.getEmail()).willReturn(email);
				given(savedMember.getNickname()).willReturn(nickname);

				given(memberRepository.findByEmail(email)).willReturn(Optional.empty());
				given(memberRepository.save(any(Member.class))).willReturn(savedMember);
				given(commonCodeService.convertNameToCommonCode(eq("로맨스"), any())).willReturn("14");
				given(commonCodeService.convertNameToCommonCode(eq("코미디"), any())).willReturn("04");
				given(commonCodeService.convertNameToCommonCode(eq("액션"), any())).willReturn("01");

				// when
				CreateMemberResponse response = memberService.signup(request);

				// then
				assertSoftly(softly -> {
					softly.assertThat(response.id()).isEqualTo(memberId);
					softly.assertThat(response.email()).isEqualTo(email);
					softly.assertThat(response.nickname()).isEqualTo(nickname);
					verify(memberRepository, times(1)).findByEmail(email);
					verify(memberRepository, times(1)).save(any(Member.class));
					verify(memberFavorGenreRepository, times(3)).save(any(MemberFavorGenre.class));
					verify(commonCodeService, times(3)).convertNameToCommonCode(anyString(), any());
				});
			}

			@Test
			void 회원가입_성공_장르선택없음() {
				// given
				String email = "test@example.com";
				String nickname = "testuser";
				LocalDate birth = LocalDate.of(2000, 1, 1);
				String socialType = "NAVER";
				String profileImage = "https://example.com/image.jpg";

				CreateMemberRequest request = new CreateMemberRequest(
						email,
						nickname,
						birth,
						socialType,
						null,  // 장르 선택 없음
						profileImage
				);

				UUID memberId = UUID.randomUUID();
				Member savedMember = mock(Member.class);
				given(savedMember.getId()).willReturn(memberId);
				given(savedMember.getEmail()).willReturn(email);
				given(savedMember.getNickname()).willReturn(nickname);

				given(memberRepository.findByEmail(email)).willReturn(Optional.empty());
				given(memberRepository.save(any(Member.class))).willReturn(savedMember);

				// when
				CreateMemberResponse response = memberService.signup(request);

				// then
				assertSoftly(softly -> {
					softly.assertThat(response.id()).isEqualTo(memberId);
					softly.assertThat(response.email()).isEqualTo(email);
					softly.assertThat(response.nickname()).isEqualTo(nickname);
					verify(memberRepository, times(1)).findByEmail(email);
					verify(memberRepository, times(1)).save(any(Member.class));
					verify(memberFavorGenreRepository, never()).save(any(MemberFavorGenre.class));
					verify(commonCodeService, never()).convertNameToCommonCode(anyString(), any());
				});
			}
		}

        @Nested
        class Failure {
            @Test
            void 이미_존재하는_이메일_MemberException_emailAlreadyExists_예외발생() {
                // given
                String email = "existing@example.com";
                CreateMemberRequest request = new CreateMemberRequest(
                        email,
                        "nickname",
                        LocalDate.of(2000, 1, 1),
                        "NAVER",
                        List.of(),
                        "https://example.com/image.jpg"
                );

                given(memberRepository.findByEmail(email))
                        .willReturn(Optional.of(mock(Member.class)));

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> memberService.signup(request))
                            .isInstanceOf(MemberException.class)
                            .hasMessage("이미 존재하는 이메일입니다.");
                });
            }

            @Test
            void 잘못된_소셜타입_MemberException_invalidSocialType_예외발생() {
                // given
                CreateMemberRequest request = new CreateMemberRequest(
                        "test@example.com",
                        "nickname",
                        LocalDate.of(2000, 1, 1),
                        "INVALID_TYPE",
                        List.of(),
                        "https://example.com/image.jpg"
                );

                // when & then
                assertSoftly(softly -> {
                    softly.assertThatThrownBy(() -> memberService.signup(request))
                            .isInstanceOf(MemberException.class)
                            .hasMessage("잘못된 소셜 로그인 타입입니다.");
                });
            }

			@Test
			void 존재하지_않는_장르명_예외발생() {
				// given
				CreateMemberRequest request = new CreateMemberRequest(
						"test@example.com",
						"nickname",
						LocalDate.of(2000, 1, 1),
						"NAVER",
						List.of("로맨틱"),  // 존재하지 않는 장르명
						"https://example.com/image.jpg"
				);

				given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
				given(memberRepository.save(any(Member.class))).willReturn(mock(Member.class));
				given(commonCodeService.convertNameToCommonCode(anyString(), any()))
						.willThrow(CommonCodeException.genreNameNotFound("로맨틱"));

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> memberService.signup(request))
							.isInstanceOf(CommonCodeException.class)
							.hasMessage("'로맨틱'에 해당하는 장르 코드가 존재하지 않습니다.");
				});
			}
        }
    }

	@Nested
	class Login {

		@Nested
		class Success {

			@Test
			void 로그인_성공() {
				// given
				String email = "test@example.com";
				String socialType = "GOOGLE";

				Member member = mock(Member.class);
				given(member.getId()).willReturn(UUID.randomUUID());
				given(member.getEmail()).willReturn(email);
				given(member.getNickname()).willReturn("testuser");
				given(member.getRoleType()).willReturn(RoleType.USER);
				given(member.getSocialType()).willReturn(SocialType.GOOGLE);

				given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

				LoginMemberRequest request = new LoginMemberRequest(email, socialType);

				// when
				LoginMemberResponse response = memberService.login(request);

				// then
				assertSoftly(softly -> {
					softly.assertThat(response.email()).isEqualTo(email);
					softly.assertThat(response.nickname()).isEqualTo("testuser");
					softly.assertThat(response.role()).isEqualTo("USER");
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
				given(member.getSocialType()).willReturn(SocialType.GOOGLE);
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

	@Nested
	class DeleteMember {
		@Nested
		class Success {
			@Test
			void 회원_탈퇴_성공() {
				// given
				UUID memberId = UUID.randomUUID();
				Member member = mock(Member.class);
				given(member.getId()).willReturn(memberId);

				Member foundMember = mock(Member.class);
				given(memberRepository.findById(memberId))
						.willReturn(Optional.of(foundMember));

				// when
				memberService.deleteMember(member);

				// then
				assertSoftly(softly -> {
					verify(foundMember, times(1)).delete();
					verify(memberRepository, times(1)).findById(memberId);
				});
			}
		}

		@Nested
		class Failure {
			@Test
			void 존재하지_않는_회원_MemberException_memberNotFound_예외발생() {
				// given
				Member member = mock(Member.class);
				given(member.getId()).willReturn(UUID.randomUUID());
				given(memberRepository.findById(any(UUID.class)))
						.willReturn(Optional.empty());

				// when & then
				assertSoftly(softly -> {
					softly.assertThatThrownBy(() -> memberService.deleteMember(member))
							.isInstanceOf(MemberException.class)
							.hasMessage("회원(Id: %s)이 존재하지 않습니다.", member.getId());
				});
			}
		}
	}

	@Test
	void updateGenre() {
	}

	@Test
	void updateImage() {
	}

	@Nested
	class UpdateNickname {

		@Nested
		class Success {

			@Test
			void 중복되지_않는_닉네임으로_변경() {
				// given -- 테스트의 상태 설정
				Member member = Member.builder()
					.email("user@user.com")
					.nickname("nickname")
					.birth(LocalDate.of(2000, 12, 25))
					.socialType(SocialType.NAVER)
					.profileImage("profileImage.jpg")
					.build();

				given(memberRepository.findById(any())).willReturn(Optional.of(member));

				PutMemberNicknameRequest request = new PutMemberNicknameRequest("newNickname");

				given(memberRepository.existsByNickname(any())).willReturn(false);

				member.updateNickname(request.nickname());

				// when -- 테스트하고자 하는 행동
				PutMemberNicknameResponse result = memberService.updateNickname(member, request);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result).isNotNull();
					softly.assertThat(result.nickname()).isEqualTo(request.nickname());
				});
			}
		}

		@Nested
		class Failure {

			@Test
			void 존재하지_않는_회원_MemberException_memberNotFound_예외발생() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);

				given(member.getId()).willReturn(UUID.randomUUID());
				given(memberRepository.findById(any())).willReturn(Optional.empty());

				// when -- 테스트하고자 하는 행동
				MemberException exception = assertThrows(MemberException.class, () ->
					memberService.updateNickname(member, new PutMemberNicknameRequest("newNickname"))
				);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
					softly.assertThat(exception.getMessage()).isEqualTo("회원(Id: %s)이 존재하지 않습니다.", member.getId());
				});
			}

			@Test
			void 중복된_닉네임_MemberException_nicknameDuplicate_예외발생() {
				// given -- 테스트의 상태 설정
				Member member = Member.builder()
					.email("user@user.com")
					.nickname("nickname")
					.birth(LocalDate.of(2000, 12, 25))
					.socialType(SocialType.NAVER)
					.profileImage("profileImage.jpg")
					.build();

				given(memberRepository.findById(any())).willReturn(Optional.of(member));

				PutMemberNicknameRequest request = new PutMemberNicknameRequest("nickname");

				given(memberRepository.existsByNickname(any())).willReturn(true);


				// when -- 테스트하고자 하는 행동
				MemberException exception = assertThrows(MemberException.class, () ->
					memberService.updateNickname(member, request)
				);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
					softly.assertThat(exception.getMessage()).isEqualTo("닉네임 중복으로 인해 변경이 실패했습니다.");
				});
			}
		}
	}

	@Test
	void updateNotice() {
	}
}