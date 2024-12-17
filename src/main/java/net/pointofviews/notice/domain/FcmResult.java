package net.pointofviews.notice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private boolean isSuccess;

    @Enumerated(EnumType.STRING)
    private FcmErrorCode errorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private NoticeSend noticeSend;

    @Builder
    private FcmResult(String token, boolean isSuccess, FcmErrorCode errorCode, NoticeSend noticeSend) {
        this.token = token;
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
        this.noticeSend = noticeSend;
    }

    public boolean isInvalidToken() {
        return errorCode != null && errorCode.isInvalidToken();
    }
}