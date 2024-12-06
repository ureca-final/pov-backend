package net.pointofviews.movie.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PeopleException extends BusinessException {

    public PeopleException(HttpStatus status, String message) {
        super(status, message);
    }

    public static PeopleException personNotFound(Long peopleId) {
        return new PeopleException(HttpStatus.NOT_FOUND, String.format("해당 인물(Id: %d)이 존재하지 않습니다.", peopleId));
    }
}
