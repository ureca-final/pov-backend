package net.pointofviews.common.exception;

import net.pointofviews.common.domain.CommonCodeId;
import org.springframework.http.HttpStatus;

public class CommonCodeException extends BusinessException {
    public CommonCodeException(HttpStatus status, String message) {
        super(status, message);
    }

    public static CommonCodeException commonCodeNotFound(String value) {
        String message = String.format("공통코드를 찾을 수 없습니다. code: %s", value);

        return new CommonCodeException(HttpStatus.NOT_FOUND, message);
    }

    public static CommonCodeException NameNotFound(String Name) {
        String message = String.format("'%s'에 해당하는 장르 및 키워드가 존재하지 않습니다.", Name);
        return new CommonCodeException(HttpStatus.NOT_FOUND, message);
    }

    public static CommonCodeException commonCodeIdError(CommonCodeId value) {
        String message = String.format("잘못된 공통코드 입니다. code: %s, codeGroup: %s", value.getCode(), value.getGroupCode());

        return new CommonCodeException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static CommonCodeException commonCodeEmptyError() {
        return new CommonCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "공통코드가 존재하지 않습니다.");
    }

    public static CommonCodeException genreNameNotFound(String description) {
        return new CommonCodeException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("장르명이 존재하지 않습니다. {%s}", description));
    }
}
