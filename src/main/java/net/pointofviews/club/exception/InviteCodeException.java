package net.pointofviews.club.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InviteCodeException extends BusinessException {

    public InviteCodeException(HttpStatus status, String message) {
        super(status, message);
    }

    public static InviteCodeException invalidLength(int length) {
        return new InviteCodeException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("유효하지 않은 초대코드 길이 value: {%d}", length));
    }
}
