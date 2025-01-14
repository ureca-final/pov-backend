package net.pointofviews.notice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeReceive extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Long noticeSendId;

    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    private String noticeTitle;

    @Column(columnDefinition = "TEXT")
    private String noticeContent;

    private Long reviewId;

    @Builder
    private NoticeReceive(boolean isRead, Member member, String noticeContent, Long noticeSendId, String noticeTitle, NoticeType noticeType, Long reviewId) {
        this.isRead = false;
        this.member = member;
        this.noticeContent = noticeContent;
        this.noticeSendId = noticeSendId;
        this.noticeTitle = noticeTitle;
        this.noticeType = noticeType;
        this.reviewId = reviewId;
    }
}