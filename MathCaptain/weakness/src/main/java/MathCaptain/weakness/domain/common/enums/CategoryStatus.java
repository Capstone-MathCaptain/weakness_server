package MathCaptain.weakness.domain.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryStatus {
    FITNESS("헬스"),
    STUDY("공부"),
    RUNNING("러닝");

    private final String value;

    CategoryStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CategoryStatus fromValue(String value) {
        for (CategoryStatus status : CategoryStatus.values()) {
            if (status.value.equals(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
