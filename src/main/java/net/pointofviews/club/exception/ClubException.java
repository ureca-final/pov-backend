package net.pointofviews.club.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClubException extends BusinessException {

    public ClubException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ClubException clubNotFound(UUID clubId) {
        return new ClubException(HttpStatus.NOT_FOUND, String.format("클럽(Id: %s)이 존재하지 않습니다.", clubId));
    }

    public static ClubException memberNotInClub() {
        return new ClubException(HttpStatus.FORBIDDEN, "클럽 멤버가 아닙니다.");
    }

    public static ClubException notClubLeader() {
        return new ClubException(HttpStatus.FORBIDDEN, "클럽장이 아닙니다.");
    }

    public static ClubException clubLeaderCannotLeave() {
        return new ClubException(HttpStatus.BAD_REQUEST, "클럽장은 클럽을 탈퇴할 수 없습니다.");
    }

    public static ClubException clubLeaderNotFound(UUID clubId) {
        return new ClubException(HttpStatus.NOT_FOUND, String.format("클럽(ID: %s)의 리더를 찾을 수 없습니다.", clubId));
    }

    public static ClubException memberAlreadyInClub() {
        return new ClubException(HttpStatus.CONFLICT, "이미 클럽에 가입된 회원입니다.");
    }
}
