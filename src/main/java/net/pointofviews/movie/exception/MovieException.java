package net.pointofviews.movie.exception;

import net.pointofviews.club.exception.ClubException;
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

    public static MovieException duplicateMovie(Integer tmdbId) {
        return new MovieException(HttpStatus.CONFLICT, String.format("영화(tmdb id: %d)는 존재하는 영화입니다.", tmdbId));
    }

    public static MovieException movieAlreadyInBookmark(Long movieId) {
        return new MovieException( HttpStatus.CONFLICT, String.format("이미 클럽 북마크에 존재하는 영화(Id: %d) 입니다.", movieId));
    }
}
