package net.pointofviews.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeywordLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(length = 2)
    private String reviewKeywordCode;

    @Builder
    private ReviewKeywordLink(Review review, String reviewKeywordCode) {
        this.review = review;
        this.reviewKeywordCode = reviewKeywordCode;
    }
}
