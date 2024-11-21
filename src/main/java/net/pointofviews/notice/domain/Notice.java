package net.pointofviews.notice.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;

import java.util.UUID;

@Entity
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID memberId;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(columnDefinition = "TEXT")
    private String noticeContent;

    private String noticeTitle;

    private boolean isActive;

    private String description;
}