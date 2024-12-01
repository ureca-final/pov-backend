package net.pointofviews.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
    private CommonCodeGroup(CodeGroupEnum codeGroupEnum) {
        this.groupCode = codeGroupEnum.getCode();
        this.name = codeGroupEnum.getName();
        this.description = codeGroupEnum.getDescription();
        this.isActive = codeGroupEnum.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonCodeGroup codeGroup = (CommonCodeGroup) o;
        return isActive() == codeGroup.isActive() && Objects.equals(getGroupCode(), codeGroup.getGroupCode()) && Objects.equals(getName(), codeGroup.getName()) && Objects.equals(getDescription(), codeGroup.getDescription());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getGroupCode());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Boolean.hashCode(isActive());
        return result;
    }

    @Override
    public String toString() {
        return "CommonCodeGroup{" +
                "description='" + description + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
