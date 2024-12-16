package net.pointofviews.movie.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class MovieLikeException extends BusinessException {

    public MovieLikeException(HttpStatus status, String message) {
        super(status, message);
    }

    public static MovieLikeException alreadyLiked(Long movieId, UUID memberId) {
        return new MovieLikeException(HttpStatus.BAD_REQUEST, "이미 좋아요 처리가 완료 되었습니다. " + movieId + ":" + memberId);
    }

    public static MovieLikeException alreadyDisliked(Long movieId, UUID memberId) {
        return new MovieLikeException(HttpStatus.BAD_REQUEST, "이미 좋아요 취소가 완료 되었습니다. " + movieId + ":" + memberId);
    }
}
