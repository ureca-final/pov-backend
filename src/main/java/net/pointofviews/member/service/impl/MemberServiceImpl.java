package net.pointofviews.member.service.impl;

import static net.pointofviews.member.exception.MemberException.*;

import net.pointofviews.auth.dto.response.CheckLoginResponse;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.member.domain.*;
import net.pointofviews.member.repository.MemberFavorGenreRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.pointofviews.member.repository.MemberFcmTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.member.dto.request.PutMemberGenreListRequest;
import net.pointofviews.member.dto.request.PutMemberNicknameRequest;
import net.pointofviews.member.dto.request.PutMemberNoticeRequest;
import net.pointofviews.member.dto.response.PutMemberGenreListResponse;
import net.pointofviews.member.dto.response.PutMemberImageResponse;
import net.pointofviews.member.dto.response.PutMemberNicknameResponse;
import net.pointofviews.member.dto.response.PutMemberNoticeResponse;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private static final String GENRE_PREFERENCES_KEY = "genre:preferences:";
    private final RedisTemplate<String, Object> redisTemplate;

    private final MemberRepository memberRepository;
    private final MemberFavorGenreRepository memberFavorGenreRepository;
    private final MemberFcmTokenRepository memberFcmTokenRepository;
    private final CommonCodeService commonCodeService;
    private final S3Service s3Service;

    @Override
    @Transactional
    public CreateMemberResponse signup(CreateMemberRequest request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw emailAlreadyExists();
        }

        // 소셜 타입 검증
        if (!Arrays.stream(SocialType.values())
                .map(Enum::name)
                .toList()
                .contains(request.socialType().toUpperCase())) {
            throw invalidSocialType();
        }

        SocialType socialType = SocialType.valueOf(request.socialType().toUpperCase());

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
        if (!request.favorGenres().isEmpty()) {
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

        // Redis에 선호 장르 저장
        if (!request.favorGenres().isEmpty()) {
            request.favorGenres().forEach(genreName -> {
                String genreCode = commonCodeService.convertNameToCommonCode(
                        genreName,
                        CodeGroupEnum.MOVIE_GENRE
                );
                String key = GENRE_PREFERENCES_KEY + genreCode;
                redisTemplate.opsForSet().add(key, savedMember.getId().toString());
            });
        }

        return new CreateMemberResponse(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getNickname()
        );
    }

    @Override
    public CheckLoginResponse login(LoginMemberRequest request) {
        // 이메일로 회원 조회
        Optional<Member> memberOptional = memberRepository.findByEmail(request.email());

        // 회원이 없는 경우
        if (memberOptional.isEmpty()) {
            return new CheckLoginResponse(false, null);
        }

        Member member = memberOptional.get();
        if (!member.getSocialType().name().equals(request.socialType())) {
            throw invalidSocialType();
        }


        List<String> favorGenreNames = memberFavorGenreRepository.findGenreCodeByMemberId(member.getId())
                .stream()
                .map(genreCode -> commonCodeService.convertCommonCodeToName(
                        genreCode,
                        CodeGroupEnum.MOVIE_GENRE
                ))
                .toList();

        // 회원이 있는 경우 정보 반환
        LoginMemberResponse memberInfo = new LoginMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getBirth(),
                favorGenreNames,
                member.getProfileImage(),
                member.getRoleType().name()
        );

        return new CheckLoginResponse(true, memberInfo);
    }

    @Override
    @Transactional
    public void deleteMember(Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        member.delete();
    }

    @Override
    @Transactional
    public PutMemberGenreListResponse updateGenre(Member loginMember, PutMemberGenreListRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        // 기존 및 요청한 장르 코드 추출
        List<String> existingGenreCodes = memberFavorGenreRepository.findGenreCodeByMemberId(member.getId());
        List<String> requestGenreCodes = request.genres().stream()
                .map(this::extractRequestGenreCode)
                .toList();

        // 추가 및 삭제할 장르 코드 추출
        Set<String> genresToAdd = findGenresToAdd(requestGenreCodes, existingGenreCodes);
        Set<String> genresToDelete = findGenresToDelete(existingGenreCodes, requestGenreCodes);

        // 삭제 및 추가 작업 처리
        if (!genresToDelete.isEmpty()) {
            memberFavorGenreRepository.deleteByMemberIdAndGenreCodeIn(member.getId(), genresToDelete);
        }

        genresToAdd.forEach(genreCode -> {
            MemberFavorGenre newFavorGenre = MemberFavorGenre.builder()
                    .member(member)
                    .genreCode(genreCode)
                    .build();

            memberFavorGenreRepository.save(newFavorGenre);
        });

        // Redis 업데이트
        existingGenreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            redisTemplate.opsForSet().remove(key, loginMember.getId().toString());
        });

        requestGenreCodes.forEach(genreCode -> {
            String key = GENRE_PREFERENCES_KEY + genreCode;
            redisTemplate.opsForSet().add(key, loginMember.getId().toString());
        });

        return new PutMemberGenreListResponse(request.genres());
    }

    private static Set<String> findGenresToAdd(List<String> requestGenreCodes, List<String> existingGenreCodes) {
        return requestGenreCodes.stream()
                .filter(genreCode -> !existingGenreCodes.contains(genreCode))
                .collect(Collectors.toSet());
    }

    private static Set<String> findGenresToDelete(List<String> existingGenreCodes, List<String> requestGenreCodes) {
        return existingGenreCodes.stream()
                .filter(genreCode -> !requestGenreCodes.contains(genreCode))
                .collect(Collectors.toSet());
    }

    private String extractRequestGenreCode(String genreName) {
        String groupCode = CodeGroupEnum.MOVIE_GENRE.getCode();
        String genreCode = memberFavorGenreRepository.findGenreCodeByGenreName(genreName, groupCode);

        if (genreCode == null) {
            throw memberGenreBadRequest(genreName);
        }

        return genreCode;
    }

    @Override
    @Transactional
    public PutMemberImageResponse updateProfileImage(Member loginMember, MultipartFile file) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        s3Service.validateImageFile(file);

		String profileImage = s3Service.getImage(member.getProfileImage());

        if (profileImage != null) {
            s3Service.deleteImage(profileImage);
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = s3Service.createUniqueFileName(originalFileName);
        String filePath = "members/" + uniqueFileName;

        String imageUrl = s3Service.saveImage(file, filePath);
        member.updateProfileImage(imageUrl);

        return new PutMemberImageResponse(member.getProfileImage());
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
    @Transactional
    public PutMemberNoticeResponse updateNotice(Member loginMember, PutMemberNoticeRequest request) {
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        member.updateNoticeActive(request.isNoticeActive());

        return new PutMemberNoticeResponse(member.isNoticeActive());
    }

    @Override
    @Transactional
    public void registerFcmToken(Member loginMember, String fcmToken) {
        memberFcmTokenRepository.findByMemberAndIsActiveTrue(loginMember)
                .ifPresentOrElse(
                        token -> token.updateToken(fcmToken),
                        () -> memberFcmTokenRepository.save(
                                MemberFcmToken.builder()
                                        .member(loginMember)
                                        .fcmToken(fcmToken)
                                        .build()
                        )
                );

        // fcm 토큰 발생시 알림 허용으로 변경
        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        member.updateNoticeActive(true);
    }
}
