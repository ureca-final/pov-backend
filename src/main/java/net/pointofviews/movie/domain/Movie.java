package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.review.domain.Review;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private String backdrop;

    private String country;

    private LocalDate released;

    private Integer tmdbId;

    private boolean hasAward;

    private boolean isAdult;

    @OneToMany(mappedBy = "movie")
    private final List<Review> reviews = new ArrayList<>();

    @Builder
    private Movie(String title, String director, String writer, String plot, String poster,
                  String country, LocalDate released, Integer tmdbId, boolean hasAward, String backdrop, boolean isAdult) {
        Assert.notNull(title, "title must not be null");
        this.title = title;
        this.director = director;
        this.writer = writer;
        this.plot = plot;
        this.poster = poster;
        this.country = country;
        this.released = released;
        this.tmdbId = tmdbId;
        this.hasAward = hasAward;
        this.backdrop = backdrop;
        this.isAdult = isAdult;
    }

    public void addReview(Review review) {
        this.reviews.add(review);

        // 무한루프 방지
        if (review.getMovie() != this) {
            review.setMovie(this);
        }
    }
}
