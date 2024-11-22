package net.pointofviews.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

	private final HttpStatus status;

	public BusinessException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
