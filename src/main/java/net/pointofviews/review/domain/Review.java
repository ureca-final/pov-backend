package net.pointofviews.review.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.SoftDeleteEntity;
import net.pointofviews.member.domain.Member;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
public class Review extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    private boolean isSpoiler;

    private boolean disabled;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
