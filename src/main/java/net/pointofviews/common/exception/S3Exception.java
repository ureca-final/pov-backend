// S3Exception.java
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
}
