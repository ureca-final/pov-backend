package net.pointofviews.club.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;

    private String description;

    private String clubImage;

    private Integer maxParticipants;

    private boolean isPublic;

    @OneToMany(mappedBy = "club")
    private final List<MemberClub> memberClubs = new ArrayList<>();

    @OneToMany(mappedBy = "club")
    private final List<ClubMovie> clubMovies = new ArrayList<>();

    @OneToMany(mappedBy = "club")
    private final List<ClubFavorGenre> clubFavorGenres = new ArrayList<>();

    @Builder
    private Club(String description, boolean isPublic, Integer maxParticipants, String name, String clubImage) {
        this.maxParticipants = validateMaxParticipants(maxParticipants);
        this.description = description;
        this.isPublic = isPublic;
        this.name = name;
        this.clubImage = clubImage;
    }

    private Integer validateMaxParticipants(Integer maxParticipants) {
        if (maxParticipants == null || maxParticipants < 1 || maxParticipants > 1000) {
            return 1000;
        }
        return maxParticipants;
    }

    public void updateClubImage(String clubImage) {
        this.clubImage = clubImage;
    }

    public void updateClub(String name, String description, @Min(2) @Max(1000) Integer integer, boolean Public) {
        this.name = name;
        this.description = description;
        this.maxParticipants = integer;
        this.isPublic = Public;
    }

    private Club(UUID id) {
        this.id = id;
    }

    public static Club generateProxy(UUID uuid) {
        return new Club(uuid);
    }
}
