package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.country.domain.Country;

import java.util.Objects;

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

    public void updateCountry(Country country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieCountry that = (MovieCountry) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCountry(), that.getCountry()) && Objects.equals(getMovie(), that.getMovie());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getCountry());
        result = 31 * result + Objects.hashCode(getMovie());
        return result;
    }
}
