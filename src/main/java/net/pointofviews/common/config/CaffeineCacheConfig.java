package net.pointofviews.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    @Primary
    @Bean(name = "cacheManagerWithTTL")
    public CaffeineCacheManager commonCodeCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("commonCode", "country");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }

    @Bean(name = "cacheManagerNoTTL")
    public CaffeineCacheManager commonCodeGroupCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("commonCodeGroup");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats());
        return cacheManager;
    }
}
