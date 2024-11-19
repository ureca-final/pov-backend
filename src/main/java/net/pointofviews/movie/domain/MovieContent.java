package net.pointofviews.movie.domain;

import jakarta.persistence.*;

@Entity
public class MovieContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    private String content;

    @Enumerated(EnumType.STRING)
    private MovieContentType contentType;
}
