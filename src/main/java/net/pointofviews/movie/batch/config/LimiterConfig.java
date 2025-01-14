package net.pointofviews.movie.batch.config;

import net.pointofviews.movie.batch.utils.ApiRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LimiterConfig {
    @Bean
    public ApiRateLimiter batchRateLimiter() {
        return new ApiRateLimiter();
    }
}
