package net.pointofviews.curation.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

@Entity
public class Curation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String theme;

    @Enumerated(EnumType.STRING)
    private CurationCategory category;

    private String title;

    private String description;
}
