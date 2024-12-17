package net.pointofviews.auth.config;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.handler.OAuth2AuthenticationSuccessHandler;
import net.pointofviews.auth.security.JwtAuthenticationFilter;
import net.pointofviews.auth.security.JwtTokenFilter;
import net.pointofviews.auth.service.CustomOauth2UserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class GlobalSecurityConfig {

    private final CorsConfig cors;
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOauth2UserService oauth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain globalSecurityFilterChain(HttpSecurity http) throws Exception {
        final RequestMatcher ignoredRequests = ignoredRequests();

        http.cors(config -> config.configurationSource(cors.corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ignoredRequests).permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, JwtAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.oauth2Login(auth -> auth
                .userInfoEndpoint(endpoint -> endpoint.userService(oauth2UserService))
                .successHandler(successHandler)
                .permitAll());

        return http.build();
    }

    private RequestMatcher ignoredRequests() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/error"),
                new AntPathRequestMatcher("/api/actuator/**"),
                new AntPathRequestMatcher("/api/auth/**"),
                new AntPathRequestMatcher("/api/docs/**"),
                new AntPathRequestMatcher("/api/v3/api-docs/**"),
                new AntPathRequestMatcher("/api/swagger-resources/**"),
                new AntPathRequestMatcher("/api/swagger-ui/**"),
                new AntPathRequestMatcher("/login/**"),
                new AntPathRequestMatcher("/oauth/**"),
                PathRequest.toStaticResources().atCommonLocations()
        );
    }
}