package net.pointofviews.auth.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SecurityException extends BusinessException {
    public SecurityException(HttpStatus status, String message) {
        super(status, message);
    }

    public static SecurityException tokenExpired() {
        return new SecurityException(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
    }

    public static SecurityException invalidToken() {
        return new SecurityException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    }
}