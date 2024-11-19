package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

@Entity
public class RecommendedMovie extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
}
