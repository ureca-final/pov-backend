package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.people.domain.People;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private People people;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private String roleName;

    private Integer displayOrder;

    @Builder
    private MovieCast(String roleName, Integer displayOrder) {
        this.roleName = roleName;
        this.displayOrder = displayOrder;
    }

    public void updatePeople(People people) {
        this.people = people;
    }

    public void updateMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieCast movieCast = (MovieCast) o;
        return Objects.equals(getId(), movieCast.getId()) && Objects.equals(getPeople(), movieCast.getPeople()) && Objects.equals(getMovie(), movieCast.getMovie()) && Objects.equals(getRoleName(), movieCast.getRoleName()) && Objects.equals(getDisplayOrder(), movieCast.getDisplayOrder());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getPeople());
        result = 31 * result + Objects.hashCode(getMovie());
        result = 31 * result + Objects.hashCode(getRoleName());
        result = 31 * result + Objects.hashCode(getDisplayOrder());
        return result;
    }
}
