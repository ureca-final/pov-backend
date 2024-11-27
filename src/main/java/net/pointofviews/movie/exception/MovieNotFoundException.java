package net.pointofviews.movie.exception;

import org.springframework.http.HttpStatus;

import net.pointofviews.common.exception.BusinessException;

public class MovieNotFoundException extends BusinessException {

	public MovieNotFoundException() {
		super(HttpStatus.NOT_FOUND, "존재하지 않는 영화입니다.");
	}

}
