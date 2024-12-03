package net.pointofviews.common.exception;

import org.springframework.http.HttpStatus;

public class S3Exception extends BusinessException {

	public S3Exception(HttpStatus status, String message) {
		super(status, message);
	}

	public static S3Exception failedToUpload(String message) {
		return new S3Exception(HttpStatus.INTERNAL_SERVER_ERROR,
			String.format("S3 업로드 실패: %s", message));
	}

	public static S3Exception failedToDelete(String message) {
		return new S3Exception(HttpStatus.INTERNAL_SERVER_ERROR,
			String.format("S3에서 이미지 삭제 중 오류 발생: %s", message));
	}

	public static S3Exception emptyImage() {
		return new S3Exception(HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
	}

	public static S3Exception invalidTotalImageSize() {
		return new S3Exception(HttpStatus.PAYLOAD_TOO_LARGE,
			"전체 파일 크기가 10MB를 초과합니다.");
	}

	public static S3Exception invalidImageSize() {
		return new S3Exception(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 2MB를 초과합니다.");
	}

	public static S3Exception invalidImageFormat() {
		return new S3Exception(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다.");
	}

	public static S3Exception emptyImageUrls() {
		return new S3Exception(HttpStatus.BAD_REQUEST, "삭제할 이미지 URL이 없습니다.");
	}

	public static S3Exception failedToParseHtml(String message) {
		return new S3Exception(HttpStatus.INTERNAL_SERVER_ERROR,
			String.format("HTML 파싱 중 오류가 발생했습니다: %s", message));
	}
}
