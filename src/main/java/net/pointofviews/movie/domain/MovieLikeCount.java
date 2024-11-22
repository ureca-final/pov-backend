package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class MovieLikeCount {
    @Id
    private Long movieId;

    private Long likeCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Builder
    public MovieLikeCount(Movie movie, Long likeCount) {
        this.movie = movie;
        this.movieId = movie.getId();
        this.likeCount = likeCount;
    }
}
