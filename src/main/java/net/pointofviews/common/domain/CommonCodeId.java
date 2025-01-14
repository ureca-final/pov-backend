package net.pointofviews.common.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeId implements Serializable {

    private String code;
    private String groupCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonCodeId that = (CommonCodeId) o;
        return Objects.equals(getCode(), that.getCode()) && Objects.equals(getGroupCode(), that.getGroupCode());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCode());
        result = 31 * result + Objects.hashCode(getGroupCode());
        return result;
    }
}
