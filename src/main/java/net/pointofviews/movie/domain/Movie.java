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

    private String plot;

    private String poster;

    private String backdrop;

    private String country;

    private LocalDate released;

    private Integer tmdbId;

    @Enumerated(EnumType.STRING)
    private KoreanFilmRating filmRating;

    @OneToMany(mappedBy = "movie")
    private final List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private final List<MovieGenre> genres = new ArrayList<>();

    @Builder
    private Movie(String title, String plot, String poster,
                  String country, LocalDate released, Integer tmdbId, String backdrop, KoreanFilmRating filmRating) {
        Assert.notNull(title, "title must not be null");
        this.title = title;
        this.plot = plot;
        this.poster = poster;
        this.country = country;
        this.released = released;
        this.tmdbId = tmdbId;
        this.backdrop = backdrop;
        this.filmRating = filmRating;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }
}
