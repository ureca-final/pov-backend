package net.pointofviews.notice.domain;

public enum FcmErrorCode {
    INVALID_REGISTRATION("InvalidRegistration"),
    NOT_REGISTERED("NotRegistered"),
    INVALID_ARGUMENT("InvalidArgument"),
    AUTHENTICATION_ERROR("AuthenticationError"),
    SERVER_ERROR("ServerError"),
    QUOTA_EXCEEDED("QuotaExceeded"),
    UNAVAILABLE("Unavailable"),
    INTERNAL_ERROR("InternalError"),
    UNKNOWN("UNKNOWN");

    private final String code;

    FcmErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static FcmErrorCode fromCode(String code) {
        for (FcmErrorCode errorCode : FcmErrorCode.values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return UNKNOWN;
    }

    public boolean isInvalidToken() {
        return this == INVALID_REGISTRATION || this == NOT_REGISTERED;
    }
}
