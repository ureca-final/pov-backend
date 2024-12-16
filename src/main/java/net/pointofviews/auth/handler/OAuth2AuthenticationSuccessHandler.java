package net.pointofviews.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CheckLoginResponse;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.exception.MovieException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = authToken.getPrincipal();

            if (oAuth2User instanceof MemberDetailsDto memberDetails) {
                Member member = memberDetails.member();
                LoginMemberRequest loginRequest = new LoginMemberRequest(member.getEmail(), member.getSocialType().name());

                ParameterizedTypeReference<BaseResponse<CheckLoginResponse>> typeRef =
                        new ParameterizedTypeReference<>() {
                        };

                String baseUrl = "http://localhost:8080/api/auth/login";
                ResponseEntity<BaseResponse<CheckLoginResponse>> loginResponse = restClient.post()
                        .uri(baseUrl)
                        .body(loginRequest)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError,
                                this::handleClientError)
                        .toEntity(typeRef);

                response.setStatus(loginResponse.getStatusCode().value());
                response.setContentType("application/json; charset=utf-8");
                response.setCharacterEncoding("utf-8");
                response.addHeader("Authorization", loginResponse.getHeaders().get("authorization").get(0));

                if (loginResponse.getBody() != null) {
                    response.getWriter().write(
                            objectMapper.writeValueAsString(loginResponse.getBody())
                    );
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) {
        try {
            String messages = new String(response.getBody().readAllBytes());

            String startKeyword = "\"status_message\":\"";
            String endKeyword = "\"";
            String message = StringUtils.substringBetween(messages, startKeyword, endKeyword);

            throw MovieException.tmdbBadRequest(message);
        } catch (IOException e) {
            throw new RuntimeException("외부 응답 읽기 실패", e);
        }
    }
}
