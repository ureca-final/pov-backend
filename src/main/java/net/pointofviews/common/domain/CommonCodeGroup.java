package net.pointofviews.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeGroup {

    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "common_code_group_name")
    private String name;

    @Column(name = "common_code_group_description")
    private String description;

    private boolean isActive;

    @Builder
    private CommonCodeGroup(String groupCode, String name, String description) {
        this.groupCode = groupCode;
        this.name = name;
        this.description = description;
        this.isActive = true;
    }
}
