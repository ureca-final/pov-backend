package net.pointofviews.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.auth.utils.JwtProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import net.pointofviews.auth.exception.SecurityException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");
        String refreshToken = extractRefreshTokenFromCookie(request.getCookies());

        if (accessToken != null && refreshToken != null) {
            try {
                // AccessToken 만료 체크
                if (jwtProvider.isTokenExpired(accessToken)) {
                    // RefreshToken 만료 체크
                    if (jwtProvider.isTokenExpired(refreshToken)) {
                        log.info("at만료, rt 만료");
                        throw SecurityException.tokenExpired();
                    }
                    // AccessToken 재발급
                    String newAccessToken = jwtProvider.reissueAccessToken(refreshToken);
                    request.setAttribute("access_token", newAccessToken);
                    response.setHeader("Authorization", newAccessToken);
                    log.info("at만료, rt 가능, at 헤더 추가");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if (response.getHeader("Authorization") != null) {
            request.setAttribute("newAccessToken", response.getHeader("Authorization"));
        }

        filterChain.doFilter(request, response);
    }

    private String extractRefreshTokenFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh-token".equals(cookie.getName())) {
                    return cookie.getValue().replace("%20", " ");
                }
            }
        }
        return null;
    }
}