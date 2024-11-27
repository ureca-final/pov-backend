package net.pointofviews.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin
        configuration.setAllowedOrigins(List.of(
                "https://point-of-views.com",
                "https://www.point-of-views.com"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin"
        ));

        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Refresh-Token");
        configuration.addExposedHeader("Content-Disposition");

        List<String> allowedMethods = Arrays.stream(HttpMethod.values())
                .map(HttpMethod::name)
                .collect(Collectors.toList());
        configuration.setAllowedMethods(allowedMethods);

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
