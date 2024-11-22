package net.pointofviews.common.dto;

import java.net.URI;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T> (
	String message,
	@JsonInclude(JsonInclude.Include.NON_NULL) T data
) {
	/* Success */
	public static <T> ResponseEntity<ApiResponse<T>> created(String redirectUrl, String message) {
		return ResponseEntity.created(URI.create(redirectUrl)).body(new ApiResponse<T>(message, null));
	}

	public static <T> ResponseEntity<ApiResponse<T>> ok(String message) {
		return ResponseEntity.ok(new ApiResponse<T>(message, null));
	}

	public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
		return ResponseEntity.ok(new ApiResponse<T>(message, data));
	}

	/* Failure */
	public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, T data) {
		return ResponseEntity.badRequest().body(new ApiResponse<T>(message, data));
	}
}
