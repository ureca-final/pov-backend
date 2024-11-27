package net.pointofviews.review.exception;

import org.springframework.http.HttpStatus;

import net.pointofviews.common.exception.BusinessException;

public class ReviewException extends BusinessException {

	public ReviewException(HttpStatus status, String message) {
		super(status, message);
	}

	public static ReviewException reviewNotFound(Long reviewId) {
		return new ReviewException(HttpStatus.NOT_FOUND, String.format("리뷰(Id: %d)는 존재하지 않습니다.", reviewId));
	}
}
