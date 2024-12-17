package net.pointofviews.common.repository;

import java.time.Duration;
import java.util.Set;

public interface RedisRepository {
    String getValue(String key);

    Set<String> getSetMembers(String key);

    void setValueWithTTL(String key, String value, Duration ttl);

    Long addToSet(String key, String value);

    Boolean setIfAbsent(String key, String value, Duration ttl);

    Set<String> getKeysByPattern(String pattern);

    Long removeFromSet(String key, String value);
}