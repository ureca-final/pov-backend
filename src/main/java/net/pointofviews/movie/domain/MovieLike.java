package net.pointofviews.movie.domain;

import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private boolean isLiked;

    @Builder
    private MovieLike(Member member, Movie movie, boolean isLiked) {
        this.member = member;
        this.movie = movie;
        this.isLiked = isLiked;
    }

    public void updateLikeStatus(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
