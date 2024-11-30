package net.pointofviews.member.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.auth.utils.JwtProvider;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.MemberService;
import org.springframework.stereotype.Service;

import static net.pointofviews.member.exception.MemberException.invalidSocialType;
import static net.pointofviews.member.exception.MemberException.memberNotFound;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public CreateMemberResponse signup(CreateMemberRequest request) {
        return null;
    }

    @Override
    public LoginMemberResponse login(LoginMemberRequest request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> memberNotFound());

        if (!member.getSocialType().name().equals(request.socialType())) {
            throw invalidSocialType();
        }

        // 응답 생성
        return new LoginMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getRoleType().name()
        );
    }

    @Override
    public void deleteMember() {

    }

    @Override
    public PutMemberGenreListResponse updateGenre(PutMemberGenreListRequest request) {
        return null;
    }

    @Override
    public PutMemberImageResponse updateImage(PutMemberImageRequest request) {
        return null;
    }

    @Override
    public PutMemberNicknameResponse updateNickname(PutMemberNicknameRequest request) {
        return null;
    }

    @Override
    public PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request) {
        return null;
    }
}
