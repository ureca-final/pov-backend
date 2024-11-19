package net.pointofviews.member.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;

@Entity
public class MemberFavorGenre extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(length = 2)
    private String genreCode;
}
