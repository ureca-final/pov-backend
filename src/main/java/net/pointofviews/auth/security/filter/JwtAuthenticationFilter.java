package net.pointofviews.auth.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.utils.JwtProvider;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,  @NonNull HttpServletResponse response,  @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                var claims = jwtProvider.parseToken(token);
                UUID memberId = UUID.fromString(claims.getPayload().getSubject());

                // 이메일로 회원 조회
                Member member = memberRepository.findById(memberId)  // findByEmail 대신 findById
                        .orElseThrow(() -> new RuntimeException("Member not found"));

                // roleType으로 권한 설정
                var authentication = new UsernamePasswordAuthenticationToken(
                        memberId.toString(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + member.getRoleType().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
