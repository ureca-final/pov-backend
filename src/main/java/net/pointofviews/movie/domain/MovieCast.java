package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.people.domain.People;

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
}
