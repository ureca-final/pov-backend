package net.pointofviews.club.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private MemberClub memberClub;

    @Column(length = 2)
    private String genreCode;

    public ClubFavorGenre(String genreCode, MemberClub memberClub) {
        this.genreCode = genreCode;
        this.memberClub = memberClub;
    }
}
