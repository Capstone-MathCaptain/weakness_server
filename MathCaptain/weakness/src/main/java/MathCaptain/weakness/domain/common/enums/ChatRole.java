package MathCaptain.weakness.domain.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ChatRole {
    USER,
    ASSISTANT;

    @JsonCreator
    public static ChatRole fromValue(String value) {
        return ChatRole.valueOf(value.toUpperCase());
    }
}
