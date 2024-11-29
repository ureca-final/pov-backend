package net.pointofviews.review.exception;

import org.springframework.http.HttpStatus;
import net.pointofviews.common.exception.BusinessException;

public class ImageException extends BusinessException {

    public ImageException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ImageException emptyImage() {
        return new ImageException(HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
    }

    public static ImageException invalidTotalImageSize() {
        return new ImageException(HttpStatus.PAYLOAD_TOO_LARGE,
                "전체 파일 크기가 10MB를 초과합니다.");
    }

    public static ImageException invalidImageSize() {
        return new ImageException(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 2MB를 초과합니다.");
    }

    public static ImageException invalidImageFormat() {
        return new ImageException(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다.");
    }

    public static ImageException emptyImageUrls() {
        return new ImageException(HttpStatus.BAD_REQUEST, "삭제할 이미지 URL이 없습니다.");
    }

    public static ImageException failedToParseHtml(String message) {
        return new ImageException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("HTML 파싱 중 오류가 발생했습니다: %s", message));
    }
}