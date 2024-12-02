package net.pointofviews.member.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.domain.MemberFavorGenre;
import net.pointofviews.member.domain.RoleType;
import net.pointofviews.member.domain.SocialType;
import net.pointofviews.member.dto.request.PutMemberGenreListRequest;
import net.pointofviews.member.dto.request.PutMemberNicknameRequest;
import net.pointofviews.member.dto.response.PutMemberGenreListResponse;
import net.pointofviews.member.dto.response.PutMemberNicknameResponse;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberFavorGenreRepository;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.impl.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberServiceImpl memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MemberFavorGenreRepository memberFavorGenreRepository;

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

	@Test
	void deleteMember() {
	}

	@Nested
	class UpdateGenre {

		@Nested
		class Success {

			@Test
			void 선호하는_장르_변경_시_기존_장르와_비교_후_장르_추가() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				given(memberRepository.findById(any())).willReturn(Optional.of(member));

				List<String> existingGenres = List.of("01", "02", "03");
				given(memberFavorGenreRepository.findGenreCodeByMemberId(any())).willReturn(existingGenres);

				List<String> newGenres = List.of("액션", "코미디", "범죄");
				PutMemberGenreListRequest request = new PutMemberGenreListRequest(newGenres);
				String groupCode = CodeGroupEnum.MOVIE_GENRE.getCode();

				given(memberFavorGenreRepository.findGenreCodeByGenreName("액션", groupCode)).willReturn("01");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("코미디", groupCode)).willReturn("04");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("범죄", groupCode)).willReturn("05");

				// when -- 테스트하고자 하는 행동
				PutMemberGenreListResponse result = memberService.updateGenre(member, request);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result.genres()).isNotNull();
					softly.assertThat(result.genres().size()).isEqualTo(3);
					softly.assertThat(result.genres()).isEqualTo(newGenres);

					verify(memberFavorGenreRepository).deleteByMemberIdAndGenreCodeIn(any(), any());
					verify(memberFavorGenreRepository, times(2)).save(any(MemberFavorGenre.class));
				});
			}

			@Test
			void 기존_장르와_선호하는_장르가_겹치지_않을_경우_추가_작업만_진행() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				given(memberRepository.findById(any())).willReturn(Optional.of(member));

				List<String> existingGenres = List.of("01", "02", "03");
				given(memberFavorGenreRepository.findGenreCodeByMemberId(any())).willReturn(existingGenres);

				List<String> newGenres = List.of("코미디", "범죄", "다큐멘터리");
				PutMemberGenreListRequest request = new PutMemberGenreListRequest(newGenres);
				String groupCode = CodeGroupEnum.MOVIE_GENRE.getCode();

				given(memberFavorGenreRepository.findGenreCodeByGenreName("코미디", groupCode)).willReturn("04");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("범죄", groupCode)).willReturn("05");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("다큐멘터리", groupCode)).willReturn("06");

				// when -- 테스트하고자 하는 행동
				PutMemberGenreListResponse result = memberService.updateGenre(member, request);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result.genres()).isNotNull();
					softly.assertThat(result.genres().size()).isEqualTo(3);
					softly.assertThat(result.genres()).isEqualTo(newGenres);

					verify(memberFavorGenreRepository, never()).deleteByMemberIdAndGenreCodeIn(member.getId(), null);
					verify(memberFavorGenreRepository, times(3)).save(any(MemberFavorGenre.class));
				});
			}

			@Test
			void 기존_장르와_요청된_장르가_동일한_경우_삭제작업_미수행() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				given(memberRepository.findById(any())).willReturn(Optional.of(member));

				// 기존 장르와 요청된 장르가 동일
				List<String> existingGenres = List.of("01", "02", "03");
				given(memberFavorGenreRepository.findGenreCodeByMemberId(any())).willReturn(existingGenres);

				List<String> newGenres = List.of("액션", "코미디", "범죄");
				PutMemberGenreListRequest request = new PutMemberGenreListRequest(newGenres);
				String groupCode = CodeGroupEnum.MOVIE_GENRE.getCode();

				given(memberFavorGenreRepository.findGenreCodeByGenreName("액션", groupCode)).willReturn("01");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("코미디", groupCode)).willReturn("02");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("범죄", groupCode)).willReturn("03");

				// when -- 테스트하고자 하는 행동
				PutMemberGenreListResponse result = memberService.updateGenre(member, request);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(result.genres()).isNotNull();
					softly.assertThat(result.genres().size()).isEqualTo(3);
					softly.assertThat(result.genres()).isEqualTo(newGenres);

					verify(memberFavorGenreRepository, never()).deleteByMemberIdAndGenreCodeIn(any(), any());
					verify(memberFavorGenreRepository, never()).save(any(MemberFavorGenre.class));
				});
			}

		}

		@Nested
		class Failure {

			@Test
			void 존재하지_않는_회원_MemberException_memberNotFound_예외발생() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				PutMemberGenreListRequest request = new PutMemberGenreListRequest(List.of("액션", "코미디", "범죄"));

				UUID memberId = UUID.randomUUID();

				given(member.getId()).willReturn(memberId);
				given(memberRepository.findById(any())).willReturn(Optional.empty());

				// when -- 테스트하고자 하는 행동
				MemberException exception = assertThrows(MemberException.class, () ->
					memberService.updateGenre(member, request)
				);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
					softly.assertThat(exception.getMessage())
						.isEqualTo(String.format("회원(Id: %s)이 존재하지 않습니다.", memberId));
				});
			}

			@Test
			void 유효하지_않은_장르_MemberException_memberGenreBadRequest_예외발생() {
				// given -- 테스트의 상태 설정
				Member member = mock(Member.class);
				PutMemberGenreListRequest request = new PutMemberGenreListRequest(List.of("액션", "코미디", "시사교양"));

				given(memberRepository.findById(any())).willReturn(Optional.of(member));

				String groupCode = CodeGroupEnum.MOVIE_GENRE.getCode();

				given(memberFavorGenreRepository.findGenreCodeByGenreName("액션", groupCode)).willReturn("01");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("코미디", groupCode)).willReturn("04");
				given(memberFavorGenreRepository.findGenreCodeByGenreName("시사교양", groupCode)).willReturn(null);

				// when -- 테스트하고자 하는 행동
				MemberException exception = assertThrows(MemberException.class, () ->
					memberService.updateGenre(member, request)
				);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
					softly.assertThat(exception.getMessage()).isEqualTo("잘못된 장르(Name: 시사교양)를 요청했습니다.");
				});
			}
		}
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