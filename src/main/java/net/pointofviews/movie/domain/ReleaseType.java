package net.pointofviews.movie.domain;

import lombok.Getter;

@Getter
public enum ReleaseType {
    PREMIERE(1, "프리미어"),
    THEATRICAL_LIMITED(2, "극장 (한정)"),
    THEATRICAL(3, "극장"),
    DIGITAL(4, "디지털"),
    PHYSICAL(5, "물리 매체"),
    TV(6, "TV");

    private final int code;
    private final String koreanName;

    ReleaseType(int code, String koreanName) {
        this.code = code;
        this.koreanName = koreanName;
    }
}
