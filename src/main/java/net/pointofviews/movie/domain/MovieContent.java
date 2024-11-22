package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class MovieContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private String content;

    @Enumerated(EnumType.STRING)
    private MovieContentType contentType;

    @Builder
    private MovieContent(Movie movie, String content, MovieContentType contentType) {
        this.movie = movie;
        this.content = content;
        this.contentType = contentType;
    }
}
