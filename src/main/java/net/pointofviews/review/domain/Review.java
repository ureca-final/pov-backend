package net.pointofviews.review.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import net.pointofviews.common.domain.SoftDeleteEntity;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.Movie;

import jakarta.persistence.Column;
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
public class Review extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    private String thumbnail;

    private String preference;

    private boolean isSpoiler;

    private boolean disabled;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Builder
    private Review(Member member, String title, String contents, String preference, boolean isSpoiler) {
        Assert.notNull(title, "Title must not be null");

        this.member = member;
        this.title = title;
        this.contents = contents;
        this.preference = preference;
        this.isSpoiler = isSpoiler;
        this.disabled = false;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;

        // 무한루프 방지
        if (!movie.getReviews().contains(this)) {
            movie.getReviews().add(this);
        }
    }

    public void update(String title, String contents) {
        Assert.notNull(title, "Title must not be null");
        Assert.notNull(contents, "Contents must not be null");

        this.title = title;
        this.contents = contents;
    }

    public void delete() {
        setDeletedAt(LocalDateTime.now());
    }
}
