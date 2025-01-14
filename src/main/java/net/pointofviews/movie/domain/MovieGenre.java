package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
        this.movie = movie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieGenre genre = (MovieGenre) o;
        return Objects.equals(getId(), genre.getId()) && Objects.equals(getMovie(), genre.getMovie()) && Objects.equals(getGenreCode(), genre.getGenreCode());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getMovie());
        result = 31 * result + Objects.hashCode(getGenreCode());
        return result;
    }
}
