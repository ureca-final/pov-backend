package net.pointofviews.notice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(columnDefinition = "TEXT")
    private String noticeContent;

    private String noticeTitle;

    private boolean isActive;

    private String description;

    @Builder
    private Notice(String description, UUID memberId, String noticeContent, String noticeTitle, NoticeType noticeType) {
        this.description = description;
        this.isActive = true;
        this.memberId = memberId;
        this.noticeContent = noticeContent;
        this.noticeTitle = noticeTitle;
        this.noticeType = noticeType;
    }
}