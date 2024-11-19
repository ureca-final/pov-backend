package net.pointofviews.movie.domain;

import jakarta.persistence.*;

@Entity
public class MovieGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Column(length = 2)
    private String genreCode;
}
