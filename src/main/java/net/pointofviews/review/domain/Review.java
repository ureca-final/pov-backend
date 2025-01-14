package net.pointofviews.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.SoftDeleteEntity;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.domain.Movie;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private ReviewPreference preference;

    private boolean isSpoiler;

    private boolean disabled;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Builder
    private Review(Member member, Movie movie, String title, String contents, String preference, boolean isSpoiler) {
        Assert.notNull(title, "Title must not be null");

        this.member = member;
        setMovie(movie);
        this.title = title;
        this.contents = contents;
        this.preference = ReviewPreference.from(preference);
        this.isSpoiler = isSpoiler;
        this.disabled = false;
    }

    private void setMovie(Movie movie) {
        this.movie = movie;

        // 무한루프 방지
        if (!movie.getReviews().contains(this)) {
            movie.getReviews().add(this);
        }
    }

    public void update(String title, String contents, String preference, boolean isSpoiler) {
        Assert.notNull(title, "Title must not be null");
        Assert.notNull(contents, "Contents must not be null");

        this.title = title;
        this.contents = contents;
        this.preference = ReviewPreference.from(preference);
        this.isSpoiler = isSpoiler;
    }

    public void toggleDisabled() {
        this.disabled = !this.disabled;
    }

    public void delete() {
        setDeletedAt(LocalDateTime.now());
    }
}
