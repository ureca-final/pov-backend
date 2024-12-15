package net.pointofviews.common.repository;

import java.time.Duration;

public interface RedisRepository {
    String getValue(String key);

    void setValueWithTTL(String key, String value, Duration ttl);

    Long addToSet(String key, String value);
}