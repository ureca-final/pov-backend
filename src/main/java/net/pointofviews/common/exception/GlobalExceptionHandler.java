package net.pointofviews.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.pointofviews.common.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error("[Error] MethodArgumentNotValidException - Message: {}", ex.getMessage());

		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
			.map(error -> String.format("Field '%s' rejected value '%s': %s",
				error.getField(),
				error.getRejectedValue(),
				error.getDefaultMessage()))
			.collect(Collectors.toList());

		return ApiResponse.badRequest(ex.getMessage(), errors);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handlerBusinessException(BusinessException ex) {
		log.error("[Error] BusinessException - HttpStatus: {}, Message: {}", ex.getStatus(), ex.getMessage());

		return ResponseEntity
			.status(ex.getStatus())
			.body(new ApiResponse<>(ex.getMessage(), null));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handlerRuntimeException(RuntimeException ex) {
		log.error("[Error] RuntimeException - Message: {}", ex.getMessage());

		return ApiResponse.internalServerError(ex.getMessage(), null);
	}
}
