package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    public MovieCountry(Country country) {
        this.country = country;
        country.updateMovieCountry(this);
    }

    public void updateMovie(Movie movie) {
        this.movie = movie;
    }
}
