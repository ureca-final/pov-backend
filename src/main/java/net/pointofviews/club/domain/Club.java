package net.pointofviews.club.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import net.pointofviews.common.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<MemberClub> memberClubs = new ArrayList<>();

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
}
