package MathCaptain.weakness.domain.Group.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GroupRole {
    LEADER("그룹장"),
    MEMBER("멤버");

    private final String value;

    GroupRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GroupRole fromValue(String value) {
        for (GroupRole role : GroupRole.values()) {
            if (role.value.equals(value) || role.name().equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
