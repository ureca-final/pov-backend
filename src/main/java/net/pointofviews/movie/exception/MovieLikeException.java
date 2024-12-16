package net.pointofviews.movie.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class MovieLikeException extends BusinessException {

    public MovieLikeException(HttpStatus status, String message) {
        super(status, message);
    }

    public static MovieLikeException alreadyLikedMovie(Long movieId) {
        return new MovieLikeException(HttpStatus.BAD_REQUEST, String.format("이미 좋아요를 누른 영화(Id: %d)입니다.", movieId));
    }

    public static MovieLikeException alreadyDislikedMovie(Long movieId) {
        return new MovieLikeException(HttpStatus.BAD_REQUEST, String.format("이미 좋아요를 취소한 영화(Id: %d)입니다.", movieId));
    }
}
