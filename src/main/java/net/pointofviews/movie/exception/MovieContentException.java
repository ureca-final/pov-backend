package net.pointofviews.movie.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MovieContentException extends BusinessException {

    public MovieContentException(HttpStatus status, String message) {
        super(status, message);
    }

    public static MovieContentException invalidMovieContentIds(int requestedSize, int foundSize) {
        return new MovieContentException(
                HttpStatus.BAD_REQUEST,
                String.format("일부 MovieContent ID가 존재하지 않습니다. 요청된 ID 수: %d, 조회된 ID 수: %d", requestedSize, foundSize)
        );
    }

    public static MovieContentException invalidContentType(Long contentId, String expectedType) {
        return new MovieContentException(
                HttpStatus.BAD_REQUEST,
                String.format("MovieContent(Id: %d)의 타입이 %s가 아닙니다.", contentId, expectedType)
        );
    }

    public static MovieContentException invalidYouTubeURL() {
        return new MovieContentException(HttpStatus.BAD_REQUEST, "유효하지 않은 URL 입니다.");
    }
}
