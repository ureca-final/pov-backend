package net.pointofviews.premiere.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EntryException extends BusinessException {

    public EntryException(HttpStatus status, String message) {
        super(status, message);
    }

    public static EntryException quantityExceeded() {
        return new EntryException(HttpStatus.CONFLICT, "시사회 응모 최대 인원 수를 초과했습니다.");
    }

    public static EntryException entryBadRequest() {
        return new EntryException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    }

    public static EntryException duplicateEntry() {
        return new EntryException(HttpStatus.CONFLICT, "이미 응모한 시사회입니다.");
    }

}
