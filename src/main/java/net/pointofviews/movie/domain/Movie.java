package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.movie.dto.request.PutMovieRequest;
import net.pointofviews.review.domain.Review;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private LocalDate released;

    private Integer tmdbId;

    @Convert(converter = FilmRatingConverter.class)
    private KoreanFilmRating filmRating;

    @OneToMany(mappedBy = "movie", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final Set<MovieCountry> countries = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    private final List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final Set<MovieGenre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final Set<MovieCrew> crews = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final Set<MovieCast> casts = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE)
    private final Set<MovieContent> contents = new HashSet<>();

    @Builder
    private Movie(String title, String plot, String poster,
                  LocalDate released, Integer tmdbId, String backdrop, KoreanFilmRating filmRating) {
        Assert.notNull(title, "title must not be null");
        this.title = title;
        this.plot = plot;
        this.poster = poster;
        this.released = released;
        this.tmdbId = tmdbId;
        this.backdrop = backdrop;
        this.filmRating = filmRating;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void addCrew(MovieCrew crew) {
        this.crews.add(crew);
        crew.updateMovie(this);
    }

    public void addCast(MovieCast cast) {
        casts.add(cast);
        cast.updateMovie(this);
    }

    public void addGenre(MovieGenre movieGenre) {
        this.genres.add(movieGenre);
        movieGenre.updateMovie(this);
    }

    public void addCountry(MovieCountry movieCountry) {
        this.countries.add(movieCountry);
        movieCountry.updateMovie(this);
    }

    public void updateMovie(PutMovieRequest request, List<MovieCast> casts, List<MovieCrew> crews, List<MovieCountry> countries, List<MovieGenre> genres) {
        this.title = request.title();
        this.plot = request.plot();
        this.released = request.release();

        this.genres.clear();
        this.countries.clear();
        this.casts.clear();
        this.crews.clear();

        this.countries.addAll(countries);
        this.genres.addAll(genres);
        this.casts.addAll(casts);
        this.crews.addAll(crews);
    }

    public void updateMovie(LocalDate release, KoreanFilmRating certification) {
        this.released = release;
        filmRating = certification;
    }
}
