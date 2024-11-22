package net.pointofviews.notice.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

@Entity
public class NoticeReceive extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member memberId;

    private Long noticeSendId;

    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    private String noticeTitle;

    @Column(columnDefinition = "TEXT")
    private String noticeContent;
}