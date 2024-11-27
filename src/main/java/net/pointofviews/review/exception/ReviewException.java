package net.pointofviews.review.exception;

import org.springframework.http.HttpStatus;

import net.pointofviews.common.exception.BusinessException;

public class ReviewException extends BusinessException {

	public ReviewException() {
		super(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다.");
	}
}
