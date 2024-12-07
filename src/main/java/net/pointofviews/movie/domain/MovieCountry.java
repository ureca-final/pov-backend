package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.country.domain.Country;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    public MovieCountry(Country country) {
        this.country = country;
    }

    public void updateMovie(Movie movie) {
        this.movie = movie;
    }
}
