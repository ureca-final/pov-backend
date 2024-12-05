package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class People {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private Integer tmdbId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Builder
    private People(String name, Movie movie, String imageUrl, Integer tmdbId) {
        Assert.notNull(name, "name must not be null");
        this.name = name;
        this.movie = movie;
        this.imageUrl = imageUrl;
        this.tmdbId = tmdbId;
    }
}