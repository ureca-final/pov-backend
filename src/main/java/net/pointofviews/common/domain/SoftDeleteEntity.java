package net.pointofviews.common.domain;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class SoftDeleteEntity extends BaseEntity {

    private LocalDateTime deletedAt;
}
