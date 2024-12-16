package net.pointofviews.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLikeCount {
    @Id
    private Long reviewId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "review_id")
    private Review review;

    private Long reviewLikeCount;

    @Builder
    private ReviewLikeCount(Review review, Long reviewLikeCount) {
        this.review = review;
        this.reviewLikeCount = (reviewLikeCount != null) ? reviewLikeCount : 0L;
    }

    public void updateCount(Long count) {
        this.reviewLikeCount = count;
    }
}
