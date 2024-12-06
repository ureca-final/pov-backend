package net.pointofviews.auth.security;

import io.jsonwebtoken.Claims;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                Jws<Claims> claims = jwtProvider.parseToken(token);
                UUID memberId = UUID.fromString(claims.getPayload().getSubject());

                // uuid로 회원 조회
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
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
