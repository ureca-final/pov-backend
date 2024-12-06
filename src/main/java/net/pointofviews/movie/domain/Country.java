package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "country")
    private final List<MovieCountry> movieCountry = new ArrayList<>();

    public Country(String name) {
        this.name = name;
    }

    public void updateMovieCountry(MovieCountry movieCountry) {
        this.movieCountry.add(movieCountry);
    }
}
