package net.pointofviews.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberFavorGenreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${oauth.baseurl}")
    private String redirectBaseUrl;

    private final MemberFavorGenreRepository memberFavorGenreRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            log.info("naver OAuth 로그인 실행");
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = authToken.getPrincipal();


            if (oAuth2User instanceof MemberDetailsDto memberDetails) {
                Member member = memberDetails.member();
                boolean exists = memberFavorGenreRepository.existsByMemberId(member.getId());

                String socialType = member.getSocialType().toString().toLowerCase();

                response.sendRedirect(redirectBaseUrl + "/oauth/" + socialType + "/callback?email=" + member.getEmail() +
                        "&image=" + member.getProfileImage() + "&exists=" + exists);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
