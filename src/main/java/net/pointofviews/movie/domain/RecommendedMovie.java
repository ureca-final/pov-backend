package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class RecommendedMovie extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Builder
    private RecommendedMovie(Member member, Movie movie) {
        this.member = member;
        this.movie = movie;
    }
}
