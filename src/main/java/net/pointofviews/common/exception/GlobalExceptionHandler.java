package net.pointofviews.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error("[Error] MethodArgumentNotValidException - Message: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handlerBusinessException(BusinessException ex) {
		log.error("[Error] BusinessException - HttpStatus: {}, Message: {}", ex.getStatus(), ex.getMessage());
		return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handlerRuntimeException(RuntimeException ex) {
		log.error("[Error] RuntimeException - Message: {}", ex.getMessage());
		return ResponseEntity.internalServerError().body(ex.getMessage());
	}

}
