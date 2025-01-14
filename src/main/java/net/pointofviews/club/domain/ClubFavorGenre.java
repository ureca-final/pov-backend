package net.pointofviews.club.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubFavorGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    @Column(length = 2)
    private String genreCode;

    @Builder
    private ClubFavorGenre(String genreCode, Club club) {
        this.genreCode = genreCode;
        this.club = club;
    }
}
