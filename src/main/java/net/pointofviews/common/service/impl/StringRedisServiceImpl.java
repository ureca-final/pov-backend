package net.pointofviews.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.exception.RedisException;
import net.pointofviews.common.repository.RedisRepository;
import net.pointofviews.common.service.RedisService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class StringRedisServiceImpl implements RedisService {

    private final RedisRepository redisRepository;

    public StringRedisServiceImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public String getValue(String key) {
        try {
            return redisRepository.getValue(key);
        } catch (Exception e) {
            log.error("키를 이용해 값을 가져오는 데 실패했습니다.: {}", key, e);
            throw RedisException.redisServerError(key);
        }
    }

    @Override
    public void setValue(String key, String value, Duration ttl) {
        try {
            redisRepository.setValueWithTTL(key, value, ttl);
        } catch (Exception e) {
            log.error("키, 값을 TTL 과 함께 등록하는 데 실패했습니다.: {}", key, e);
            throw RedisException.redisServerError(key);
        }
    }

    @Override
    public Long addToSet(String key, String value) {
        try {
            Long result = redisRepository.addToSet(key, value);
            if (result == null) {
                throw RedisException.redisServerError(key);
            }
            return result;
        } catch (Exception e) {
            log.error("set 에 키, 값을 추가하는 데 실패했습니다.: {}", key, e);
            throw RedisException.redisServerError(key);
        }
    }

    @Override
    public Boolean setIfAbsent(String key, String value, Duration ttl) {
        Boolean result = redisRepository.setIfAbsent(key, value, ttl);
        if (result == null) {
            log.warn("Redis setIfAbsent: null 반환, key={}, value={}", key, value);
            throw RedisException.redisServerError(key);
        }
        return result;
    }

    @Override
    public Set<String> getKeys(String pattern) {
        try {
            Set<String> keys = redisRepository.getKeysByPattern(pattern);
            if (keys == null) {
                return Collections.emptySet();
            }
            return keys;
        } catch (Exception e) {
            log.error("패턴으로 키를 조회하는데 실패했습니다.: {}", pattern, e);
            throw RedisException.redisServerError(pattern);
        }
    }
}
