package net.pointofviews.premiere.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

@Entity
public class Entry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Premiere premiere;
}
