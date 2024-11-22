package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Builder
    private Actor(String name, Movie movie) {
        Assert.notNull(name, "name must not be null");
        this.name = name;
        this.movie = movie;
    }
}