package net.pointofviews.movie.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieLikeCount {
    @Id
    private Long movieId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private Long likeCount;

    @Builder
    private MovieLikeCount(Movie movie, Long likeCount) {
        this.movie = movie;
        this.likeCount = (likeCount != null) ? likeCount : 0L;
    }
}
