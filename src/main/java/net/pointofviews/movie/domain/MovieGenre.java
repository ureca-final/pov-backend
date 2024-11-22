package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
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
}
