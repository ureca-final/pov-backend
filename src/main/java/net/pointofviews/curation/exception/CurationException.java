package net.pointofviews.curation.exception;

import net.pointofviews.club.exception.ClubException;
import net.pointofviews.member.exception.MemberException;
import org.springframework.http.HttpStatus;
import net.pointofviews.common.exception.BusinessException;

public class CurationException extends BusinessException {

    public CurationException(HttpStatus status, String message) {super(status, message);}

    public static CurationException CurationNotFound() {
        return new CurationException(HttpStatus.NOT_FOUND, "존재하지 않는 큐레이션입니다.");
    }

    public static CurationException CurationIdNotFound(Long id) {
        return new CurationException(HttpStatus.NOT_FOUND, String.format("큐레이션(Id: %s)이 존재하지 않습니다.", id));
    }

    public static CurationException CurationAlreadyExists(Long curationId) {
        return new CurationException(HttpStatus.CONFLICT, "큐레이션 ID " + curationId + "에 해당하는 데이터가 이미 존재합니다.");
    }

}