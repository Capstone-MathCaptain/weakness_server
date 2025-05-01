package MathCaptain.weakness.domain.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecruitmentStatus {
    // 모집중, 모집종료
    RECRUITING("모집중"),
    END("모집종료");

    private final String value;

    RecruitmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RecruitmentStatus fromValue(String value) {
        for (RecruitmentStatus status : RecruitmentStatus.values()) {
            if (status.value.equals(value) || status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
