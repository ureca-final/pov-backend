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
public class NoticeSend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Notice notice;

    private boolean isSucceed;

    @Column(columnDefinition = "TEXT")
    private String noticeContentDetail;

    @Builder
    private NoticeSend(boolean isSucceed, Notice notice, String noticeContentDetail) {
        this.isSucceed = isSucceed;
        this.notice = notice;
        this.noticeContentDetail = noticeContentDetail;
    }
}