package net.pointofviews.club.domain;

import java.util.UUID;

import net.pointofviews.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
