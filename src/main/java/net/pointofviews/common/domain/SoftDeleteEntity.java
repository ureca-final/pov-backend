package net.pointofviews.common.domain;

import java.time.LocalDateTime;

public abstract class SoftDeleteEntity extends BaseEntity {

    private LocalDateTime deletedAt;
}
