package net.pointofviews.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.auth.utils.JwtProvider;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import net.pointofviews.auth.exception.SecurityException;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                processTokenAuthentication(token, request);
            } catch (ExpiredJwtException e) {
                handleExpiredToken(request, e);
            } catch (Exception e) {
                handleInvalidToken(response);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void processTokenAuthentication(String token, HttpServletRequest request) {
        Jws<Claims> claims = jwtProvider.parseToken(token);
        UUID memberId = UUID.fromString(claims.getPayload().getSubject());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MemberDetailsDto memberDetails = MemberDetailsDto.from(member);

        // roleType으로 권한 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                memberDetails,
                null,
                memberDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleExpiredToken(HttpServletRequest request, ExpiredJwtException e) {
        String newAccessToken = (String) request.getAttribute("newAccessToken");
        if (newAccessToken != null) {
            processTokenAuthentication(newAccessToken, request);
        } else {
            SecurityContextHolder.clearContext();
            throw SecurityException.tokenExpired();
        }
    }

    private void handleInvalidToken(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        throw SecurityException.invalidToken();
    }
}
