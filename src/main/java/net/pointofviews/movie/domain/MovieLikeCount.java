package net.pointofviews.movie.domain;

import jakarta.persistence.*;

@Entity
public class MovieLikeCount {
    @Id
    private Long movieId;

    private Long likeCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;
}
