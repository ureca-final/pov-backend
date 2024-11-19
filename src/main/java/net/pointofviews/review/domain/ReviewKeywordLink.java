package net.pointofviews.review.domain;

import jakarta.persistence.*;

@Entity
public class ReviewKeywordLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(length = 2)
    private String reviewKeywordCode;
}
