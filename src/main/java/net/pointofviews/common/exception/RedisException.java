package net.pointofviews.common.exception;

import org.springframework.http.HttpStatus;

public class RedisException extends BusinessException {
    private RedisException(HttpStatus status, String message) {
        super(status, message);
    }

    public static RedisException redisServerError() {
        return new RedisException(HttpStatus.INTERNAL_SERVER_ERROR, "레디스 서버 오류");
    }
}
