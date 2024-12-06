package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private People people;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private String roleName;

    private Integer displayOrder;

    @Builder
    public MovieCast(String roleName, Integer displayOrder) {
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
