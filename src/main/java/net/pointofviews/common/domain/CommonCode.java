package net.pointofviews.common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode {
    @Id
    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "group_code")
    private CommonCodeGroup groupCode;

    @Column(name = "common_code_name")
    private String name;

    @Column(name = "common_code_description")
    private String description;

    private boolean isActive;

    @Builder
    public CommonCode(String code, CommonCodeGroup groupCode, String name, String description) {
        this.code = code;
        this.groupCode = groupCode;
        this.name = name;
        this.description = description;
        this.isActive = true;
    }
}
