package net.pointofviews.common.dto;

import java.net.URI;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

public record BaseResponse<T> (
	String message,
	@JsonInclude(JsonInclude.Include.NON_NULL) T data
) {
	/* Success */
	public static <T> ResponseEntity<BaseResponse<T>> created(String redirectUrl, String message) {
		return ResponseEntity.created(URI.create(redirectUrl)).body(new BaseResponse<T>(message, null));
	}

	public static <T> ResponseEntity<BaseResponse<T>> ok(String message) {
		return ResponseEntity.ok(new BaseResponse<T>(message, null));
	}

	public static <T> ResponseEntity<BaseResponse<T>> ok(String message, T data) {
		return ResponseEntity.ok(new BaseResponse<T>(message, data));
	}

	/* Failure */
	public static <T> ResponseEntity<BaseResponse<T>> badRequest(String message, T data) {
		return ResponseEntity.badRequest().body(new BaseResponse<T>(message, data));
	}

	public static <T> ResponseEntity<BaseResponse<T>> internalServerError(String message, T data) {
		return ResponseEntity.internalServerError().body(new BaseResponse<T>(message, data));
	}
}
