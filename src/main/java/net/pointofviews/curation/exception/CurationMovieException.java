package net.pointofviews.curation.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CurationMovieException extends BusinessException {

    public CurationMovieException(HttpStatus status, String message) {super(status, message);}

    public static CurationMovieException CurationMovieKeyNotFound() {
        return new CurationMovieException(HttpStatus.NOT_FOUND, "존재하지 않는 Key 입니다.");
    }

}