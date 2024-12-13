package net.pointofviews.country.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CountryException extends BusinessException {

    public CountryException(HttpStatus status, String message) {
        super(status, message);
    }

    public static CountryException notFound(String country) {
        return new CountryException(HttpStatus.NOT_FOUND, String.format("국가 정보를 찾을 수 없습니다. value: {%s}", country));
    }
}
