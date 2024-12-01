package net.pointofviews.review.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ReviewImageException extends BusinessException {
    public ReviewImageException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ReviewImageException emptyImage() {
        return new ReviewImageException(HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
    }

    public static ReviewImageException invalidTotalImageSize() {
        return new ReviewImageException(HttpStatus.PAYLOAD_TOO_LARGE,
                "전체 파일 크기가 10MB를 초과합니다.");
    }

    public static ReviewImageException invalidImageSize() {
        return new ReviewImageException(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 2MB를 초과합니다.");
    }

    public static ReviewImageException invalidImageFormat() {
        return new ReviewImageException(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다.");
    }

    public static ReviewImageException emptyImageUrls() {
        return new ReviewImageException(HttpStatus.BAD_REQUEST, "삭제할 이미지 URL이 없습니다.");
    }

    public static ReviewImageException failedToParseHtml(String message) {
        return new ReviewImageException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("HTML 파싱 중 오류가 발생했습니다: %s", message));
    }
}