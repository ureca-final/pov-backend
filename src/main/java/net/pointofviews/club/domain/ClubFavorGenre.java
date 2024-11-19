package net.pointofviews.club.domain;

import jakarta.persistence.*;

@Entity
public class ClubFavorGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberClub memberClub;

    @Column(length = 2)
    private String genreCode;
}
