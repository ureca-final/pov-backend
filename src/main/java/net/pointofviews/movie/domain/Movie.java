package net.pointofviews.movie.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import net.pointofviews.review.domain.Review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String director;

    private String writer;

    private String plot;

    private String poster;

    private String country;

    private LocalDateTime released;

    private String imdbId;

    private boolean hasAward;

    @OneToMany(mappedBy = "movie")
    private final List<Review> reviews = new ArrayList<>();

    @Builder
    private Movie(String title, String director, String writer, String plot, String poster,
                  String country, LocalDateTime released, String imdbId, boolean hasAward) {
        Assert.notNull(title, "title must not be null");
        this.title = title;
        this.director = director;
        this.writer = writer;
        this.plot = plot;
        this.poster = poster;
        this.country = country;
        this.released = released;
        this.imdbId = imdbId;
        this.hasAward = hasAward;
    }

    public void addReview(Review review) {
        this.reviews.add(review);

        // 무한루프 방지
        if (review.getMovie() != this) {
            review.setMovie(this);
        }
    }
}
