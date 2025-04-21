package MathCaptain.weakness.domain.User.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Tiers {
    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    PLATINUM("플레티넘"),
    DIAMOND("다이아몬드"),
    MASTER("마스터");

    private final String value;

    Tiers(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Tiers fromValue(String value) {
        for (Tiers tier : Tiers.values()) {
            if (tier.value.equals(value) || tier.name().equals(value)) {
                return tier;
            }
        }
        throw new IllegalArgumentException("정의되지 않은 값 입니다 : " + value);
    }
}
