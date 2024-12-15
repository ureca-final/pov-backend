package net.pointofviews.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.exception.RedisException;
import net.pointofviews.common.repository.RedisRepository;
import net.pointofviews.common.service.RedisService;
import org.springframework.stereotype.Service;

import java.time.Duration;

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
}
