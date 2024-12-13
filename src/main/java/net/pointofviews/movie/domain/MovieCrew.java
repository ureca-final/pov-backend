package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.people.domain.People;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCrew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private People people;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private String role;

    public MovieCrew(String role) {
        this.role = role;
    }

    public void updateMovie(Movie movie) {
        this.movie = movie;
    }

    public void updatePeople(People people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieCrew movieCrew = (MovieCrew) o;
        return Objects.equals(getId(), movieCrew.getId()) && Objects.equals(getPeople(), movieCrew.getPeople()) && Objects.equals(getMovie(), movieCrew.getMovie()) && Objects.equals(getRole(), movieCrew.getRole());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getPeople());
        result = 31 * result + Objects.hashCode(getMovie());
        result = 31 * result + Objects.hashCode(getRole());
        return result;
    }
}
