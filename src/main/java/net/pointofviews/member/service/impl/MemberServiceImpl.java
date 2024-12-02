package net.pointofviews.member.service.impl;

import static net.pointofviews.member.exception.MemberException.*;

import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.member.domain.MemberFavorGenre;
import net.pointofviews.member.domain.RoleType;
import net.pointofviews.member.domain.SocialType;
import net.pointofviews.member.repository.MemberFavorGenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.member.dto.request.PutMemberGenreListRequest;
import net.pointofviews.member.dto.request.PutMemberImageRequest;
import net.pointofviews.member.dto.request.PutMemberNicknameRequest;
import net.pointofviews.member.dto.request.PutMemberNoticeRequest;
import net.pointofviews.member.dto.response.PutMemberGenreListResponse;
import net.pointofviews.member.dto.response.PutMemberImageResponse;
import net.pointofviews.member.dto.response.PutMemberNicknameResponse;
import net.pointofviews.member.dto.response.PutMemberNoticeResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberFavorGenreRepository memberFavorGenreRepository;
    private final CommonCodeService commonCodeService;

    @Override
    @Transactional
    public CreateMemberResponse signup(CreateMemberRequest request) {
        // 이메일 중복 검사
        memberRepository.findByEmail(request.email())
                .ifPresent(member -> {
                    throw emailAlreadyExists();
                });

        // 소셜 타입 검증
        SocialType socialType;
        try {
            socialType = SocialType.valueOf(request.socialType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw invalidSocialType();
        }

        Member member = Member.builder()
                .email(request.email())
                .nickname(request.nickname())
                .birth(request.birth())
                .socialType(socialType)
                .profileImage(request.profileImage())
                .roleType(RoleType.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        // 관심 장르 저장
        if (request.favorGenres() != null && !request.favorGenres().isEmpty()) {
            request.favorGenres().forEach(genreName -> {
                String genreCode = commonCodeService.convertNameToCommonCode(
                        genreName,
                        CodeGroupEnum.MOVIE_GENRE
                );

                MemberFavorGenre favorGenre = MemberFavorGenre.builder()
                        .member(savedMember)
                        .genreCode(genreCode)
                        .build();
                memberFavorGenreRepository.save(favorGenre);
            });
        }

        return new CreateMemberResponse(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getNickname()
        );
    }

    @Override
    public LoginMemberResponse login(LoginMemberRequest request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(MemberException::memberNotFound);

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
    @Transactional
    public void deleteMember(Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        member.delete();
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
    @Transactional
    public PutMemberNicknameResponse updateNickname(Member loginMember, PutMemberNicknameRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
            .orElseThrow(() -> memberNotFound(loginMember.getId()));

        if (memberRepository.existsByNickname(request.nickname())) {
            throw nicknameDuplicate();
        }

        member.updateNickname(request.nickname());

        return new PutMemberNicknameResponse(member.getNickname());
    }

    @Override
    public PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request) {
        return null;
    }
}
