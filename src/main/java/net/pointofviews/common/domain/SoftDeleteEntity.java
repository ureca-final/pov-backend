package net.pointofviews.common.domain;

import lombok.Getter;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class SoftDeleteEntity extends BaseEntity {

    private LocalDateTime deletedAt;

    protected void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
