package MathCaptain.weakness.domain.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RequestStatus {
    WAITING("가입요청 중"),
    ACCEPTED("가입요청 수락"),
    REJECTED("가입요청 거절"),
    CANCELED("가입요청 취소");

    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RequestStatus fromValue(String value) {
        for (RequestStatus status : RequestStatus.values()) {
            if (status.value.equals(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
