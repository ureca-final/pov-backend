package net.pointofviews.member.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import net.pointofviews.common.exception.BusinessException;

public class MemberException extends BusinessException {

    public MemberException(HttpStatus status, String message) {
        super(status, message);
    }

    public static MemberException memberNotFound() {
        return new MemberException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }

    public static MemberException memberNotFound(UUID memberId) {
        return new MemberException(HttpStatus.NOT_FOUND, String.format("회원(Id: %s)이 존재하지 않습니다.", memberId));
    }

    public static MemberException adminNotFound(UUID memberId) {
        return new MemberException(HttpStatus.NOT_FOUND, String.format("관리자(Id: %s)가 존재하지 않습니다.", memberId));
    }

    public static MemberException invalidSocialType() {
        return new MemberException(HttpStatus.BAD_REQUEST, "잘못된 소셜 로그인 타입입니다.");
    }

    public static MemberException nicknameDuplicate() {
        return new MemberException(HttpStatus.CONFLICT, "닉네임 중복으로 인해 변경이 실패했습니다.");
    }

    public static MemberException memberGenreBadRequest(String genreName) {
        return new MemberException(HttpStatus.BAD_REQUEST, String.format("잘못된 장르(Name: %s)를 요청했습니다.", genreName));
    }

    public static MemberException emailAlreadyExists() {
        return new MemberException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
    }
}
