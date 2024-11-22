package net.pointofviews.notice.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;

import java.time.LocalDateTime;

@Entity
public class NoticeSend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Notice notice;

    private boolean isSucceed;

    @Column(columnDefinition = "TEXT")
    private String noticeContentDetail;
}