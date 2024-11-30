package net.pointofviews.member.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MemberException extends BusinessException {
    public MemberException(HttpStatus status, String message) {
        super(status, message);
    }

    public static MemberException memberNotFound() {
        return new MemberException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }

    public static MemberException invalidSocialType() {
        return new MemberException(HttpStatus.BAD_REQUEST, "잘못된 소셜 로그인 타입입니다.");
    }
}
