package net.pointofviews.review.domain;

import jakarta.persistence.*;

@Entity
public class ReviewLikeCount {
    @Id
    private Long reviewId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "review_id")
    private Review review;

    private Long reviewLikeCount;
}
