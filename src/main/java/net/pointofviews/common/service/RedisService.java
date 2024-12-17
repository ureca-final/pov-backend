package net.pointofviews.common.service;

import java.time.Duration;
import java.util.Set;

public interface RedisService {
    String getValue(String key);
    Set<String> getSetMembers(String key);
    void setValue(String key, String value, Duration ttl);
    Long addToSet(String key, String value);
    Boolean setIfAbsent(String key, String value, Duration ttl);
    Set<String> getKeys(String pattern);
    Long removeFromSet(String key, String value);
}