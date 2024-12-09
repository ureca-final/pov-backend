package net.pointofviews.premiere.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PremiereException extends BusinessException {

    public PremiereException(HttpStatus status, String message) {
        super(status, message);
    }

    public static PremiereException premiereNotFound (Long premiereId) {
        return new PremiereException(HttpStatus.NOT_FOUND, String.format("시사회(Id: %d)가 존재하지 않습니다.", premiereId));
    }
}
