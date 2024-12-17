package net.pointofviews.common.exception;

import org.springframework.http.HttpStatus;

public class UuidException extends BusinessException{
    public UuidException(HttpStatus status, String message) {
        super(status, message);
    }

    public static UuidException invalidUuid(String uuid) {
        return new UuidException(HttpStatus.BAD_REQUEST, String.format("유효하지 않은 UUID: %s",uuid));
    }
}
