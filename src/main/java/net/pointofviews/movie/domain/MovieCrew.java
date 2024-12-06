package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCrew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
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
}