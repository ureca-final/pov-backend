package net.pointofviews.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@Schema(description = "공통 응답 포맷")
public record BaseResponse<T>(
        @Schema(description = "응답 메시지")
        String message,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "응답 데이터")
        T data
) {
    /* Success */
    public static <T> ResponseEntity<BaseResponse<T>> created(String redirectUrl, String message) {
        return ResponseEntity.created(URI.create(redirectUrl)).body(new BaseResponse<>(message, null));
    }

    public static <T> ResponseEntity<BaseResponse<T>> ok(String message) {
        return ResponseEntity.ok(new BaseResponse<>(message, null));
    }

    public static <T> ResponseEntity<BaseResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(new BaseResponse<>(message, data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> noContent() {
        return ResponseEntity.noContent().build();
    }

    /* Failure */
    public static <T> ResponseEntity<BaseResponse<T>> badRequest(String message, T data) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(message, data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> internalServerError(String message, T data) {
        return ResponseEntity.internalServerError().body(new BaseResponse<>(message, data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> redirection(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, url);
        return ResponseEntity.status(HttpStatus.FOUND)
                .headers(headers)
                .body(null);
    }
}