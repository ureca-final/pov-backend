package net.pointofviews.common.repository.impl;

import net.pointofviews.common.repository.RedisRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Repository
public class StringRedisRepositoryImpl implements RedisRepository {

    private final StringRedisTemplate redisTemplate;

    public StringRedisRepositoryImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Set<String> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void setValueWithTTL(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Long addToSet(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public Boolean setIfAbsent(String key, String value, Duration ttl) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
    }

    @Override
    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public Long removeFromSet(String key, String value) {
        return redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public boolean deleteKeysByPattern(String pattern) {
        return Boolean.TRUE.equals(redisTemplate.delete(pattern));
    }

    @Override
    public boolean setTTL(String key, Duration timeout) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout));
    }
}