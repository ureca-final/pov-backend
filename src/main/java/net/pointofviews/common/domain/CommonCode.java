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

    @EmbeddedId
    private CommonCodeId code;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupCode")
    @JoinColumn(name = "group_code")
    private CommonCodeGroup groupCode;

    @Column(name = "common_code_name")
    private String name;

    @Column(name = "common_code_description")
    private String description;

    private boolean isActive;

    @Builder
    public CommonCode(String code, CommonCodeGroup groupCode, String name, String description) {
        this.code = new CommonCodeId(code, groupCode.getGroupCode());
        this.groupCode = groupCode;
        this.name = name;
        this.description = description;
        this.isActive = true;
    }
}
