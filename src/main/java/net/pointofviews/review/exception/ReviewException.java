package net.pointofviews.review.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ReviewException extends BusinessException {

    public ReviewException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ReviewException reviewNotFound(Long reviewId) {
        return new ReviewException(HttpStatus.NOT_FOUND, String.format("리뷰(Id: %d)는 존재하지 않습니다.", reviewId));
    }

    public static ReviewException unauthorizedReview() {
        return new ReviewException(HttpStatus.FORBIDDEN, "리뷰에 대한 권한이 없습니다.");
    }

    public static ReviewException undefinedPreference(String preference) {
        return new ReviewException(HttpStatus.NOT_FOUND, String.format("존재하지 않는 선호 표기입니다. value: {%s}", preference));
    }

    public static ReviewException alreadyLikedReview(Long reviewId) {
        return new ReviewException(HttpStatus.BAD_REQUEST, String.format("이미 좋아요를 누른 리뷰(Id: %d)입니다.", reviewId));
    }

    public static ReviewException alreadyDislikedReview(Long reviewId) {
        return new ReviewException(HttpStatus.BAD_REQUEST, String.format("이미 좋아요를 취소한 리뷰(Id: %d)입니다.", reviewId));
    }
}
