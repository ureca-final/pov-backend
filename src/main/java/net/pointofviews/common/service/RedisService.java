package net.pointofviews.common.service;

import java.time.Duration;

public interface RedisService {
    String getValue(String key);
    void setValue(String key, String value, Duration ttl);
    Long addToSet(String key, String value);
    Boolean setIfAbsent(String key, String value, Duration ttl);
}