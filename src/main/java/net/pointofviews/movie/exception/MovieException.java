package net.pointofviews.movie.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MovieException extends BusinessException {

    public MovieException(HttpStatus status, String message) {
        super(status, message);
    }

    public static MovieException movieNotFound(Long movieId) {
        return new MovieException(HttpStatus.NOT_FOUND, String.format("영화(Id: %d)는 존재하지 않습니다.", movieId));
    }

    public static MovieException tmdbBadRequest(String message) {
        return new MovieException(HttpStatus.BAD_REQUEST, message);
    }
}
