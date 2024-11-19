package net.pointofviews.club.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import net.pointofviews.common.domain.BaseEntity;

import java.util.UUID;

@Entity
public class Club extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    private String name;

    private String description;

    private Integer maxParticipants;

    private boolean isPublic;
}
