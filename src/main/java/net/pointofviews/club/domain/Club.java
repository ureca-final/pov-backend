package net.pointofviews.club.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;

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

    private Integer maxParticipants;

    private boolean isPublic;

    @Builder
    private Club(String description, boolean isPublic, Integer maxParticipants, String name) {
        this.maxParticipants = validateMaxParticipants(maxParticipants);
        this.description = description;
        this.isPublic = isPublic;
        this.name = name;
    }

    private Integer validateMaxParticipants(Integer maxParticipants) {
        if (maxParticipants == null || maxParticipants < 1 || maxParticipants > 1000) {
            return 1000;
        }
        return maxParticipants;
    }
}
