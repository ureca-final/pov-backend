package net.pointofviews.notice.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NoticeException extends BusinessException {

    public NoticeException(HttpStatus status, String message) {
        super(status, message);
    }

    public static class NoticeNotFoundException extends NoticeException {
        public NoticeNotFoundException() {
            super(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다.");
        }
    }

    public static class NoticeSendFailedException extends NoticeException {
        public NoticeSendFailedException() {
            super(HttpStatus.INTERNAL_SERVER_ERROR, "알림 전송에 실패했습니다.");
        }
    }

    public static class NoticeTemplateNotFoundException extends NoticeException {
        public NoticeTemplateNotFoundException() {
            super(HttpStatus.NOT_FOUND, "알림 템플릿을 찾을 수 없습니다.");
        }
    }

    public static class InactiveNoticeTemplateException extends NoticeException {
        public InactiveNoticeTemplateException() {
            super(HttpStatus.BAD_REQUEST, "비활성화된 알림 템플릿입니다.");
        }
    }

    public static class NoTargetMembersFoundException extends NoticeException {
        public NoTargetMembersFoundException() {
            super(HttpStatus.NOT_FOUND, "알림을 받을 대상자가 없습니다.");
        }
    }

    public static class NoticeReceiveSaveFailedException extends NoticeException {
        public NoticeReceiveSaveFailedException() {
            super(HttpStatus.INTERNAL_SERVER_ERROR, "알림 수신 정보 저장에 실패했습니다.");
        }
    }

    public static class RedisOperationFailedException extends NoticeException {
        public RedisOperationFailedException() {
            super(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 작업 중 오류가 발생했습니다.");
        }
    }
}