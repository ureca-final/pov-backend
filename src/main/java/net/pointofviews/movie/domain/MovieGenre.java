package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Column(length = 2)
    private String genreCode;

    @Builder
    private MovieGenre(Movie movie, String genreCode) {
        if (genreCode == null || genreCode.length() != 2)
            throw new IllegalArgumentException("GenreCode must be exactly 2 characters");
        this.movie = movie;
        this.genreCode = genreCode;
    }

    public void updateMovie(Movie movie) {
        movie.addGenre(this);
        this.movie = movie;
    }
}
