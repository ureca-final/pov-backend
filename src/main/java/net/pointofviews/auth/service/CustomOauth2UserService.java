package net.pointofviews.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.auth.dto.NaverUserDetails;
import net.pointofviews.auth.dto.OAuth2UserInfo;
import net.pointofviews.auth.exception.SecurityException;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.domain.RoleType;
import net.pointofviews.member.domain.SocialType;
import net.pointofviews.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;

        if (provider.equals("naver")) {
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
        } else {
            throw SecurityException.invalidOAuto2Provider(provider);
        }

        String nickname = oAuth2UserInfo.getNickname();
        String profileImage = oAuth2UserInfo.getProfileImage();
        String email = oAuth2UserInfo.getEmail();
        String birthday = oAuth2UserInfo.getBirthday();
        String birthYear = oAuth2UserInfo.getBirthYear();

        LocalDate birth = LocalDate.parse(birthYear + "-" + birthday);

        Optional<Member> findMember = memberRepository.findByEmail(email);
        Member member;

        if (findMember.isEmpty()) {
            member = Member.builder()
                    .email(email)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .socialType(SocialType.NAVER)
                    .roleType(RoleType.USER)
                    .birth(birth)
                    .build();
            memberRepository.save(member);
        } else {
            member = findMember.get();
        }

        return new MemberDetailsDto(member, oAuth2User.getAttributes());
    }
}