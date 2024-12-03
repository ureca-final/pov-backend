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
    @Bean(name = "commonCodeCacheManager")
    public CaffeineCacheManager commonCodeCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("commonCode");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }

    @Bean(name = "commonCodeGroupCacheManager")
    public CaffeineCacheManager commonCodeGroupCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("commonCodeGroup");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats());
        return cacheManager;
    }

    @Bean(name = "MovieCurationCacheManager")
    public CaffeineCacheManager MovieCurationCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("curationMovies");
        cacheManager.setCaffeine(Caffeine.newBuilder()
//                .expireAfterWrite(10, TimeUnit.MINUTES) // TTL 고민중
                        .maximumSize(1000) // 캐시 최대 크기 설정
                        .recordStats()
        );
        return cacheManager;
    }
}
