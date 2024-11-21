package net.pointofviews.common.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class SoftDeleteEntity extends BaseEntity {

    private LocalDateTime deletedAt;
}
