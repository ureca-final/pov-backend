package net.pointofviews.common.exception;

import org.springframework.http.HttpStatus;

public class RedisException extends BusinessException {
    private RedisException(HttpStatus status, String message) {
        super(status, message);
    }

    public static RedisException redisServerError(String key) {
        return new RedisException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("레디스 작업에 실패했습니다. key: {%s}", key));
    }
}
