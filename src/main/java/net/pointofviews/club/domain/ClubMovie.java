package net.pointofviews.club.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.movie.domain.Movie;

@Entity
public class ClubMovie extends BaseEntity {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;
}
