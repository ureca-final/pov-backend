package net.pointofviews.common.exception;

import net.pointofviews.common.domain.CommonCodeGroup;
import org.springframework.http.HttpStatus;

public class CommonCodeGroupException extends BusinessException {
    public CommonCodeGroupException(HttpStatus status, String message) {
        super(status, message);
    }

    public static CommonCodeGroupException commonCodeGroupSizeSyncError() {
        return new CommonCodeGroupException(HttpStatus.INTERNAL_SERVER_ERROR, "DB와 ENUM의 공통코드 갯수가 다릅니다.");
    }

    public static CommonCodeGroupException enumAndDbCodeGroupMismatch(CommonCodeGroup dbValue) {
        String message = String.format("공통코드 그룹을 찾을 수 없습니다. dbCode: %s", dbValue.getGroupCode());

        return new CommonCodeGroupException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
