package MathCaptain.weakness.User.domain;

import MathCaptain.weakness.User.enums.Tiers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {
    @Test
    public void testTierUpgrade() {
        Users user = Users.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .nickname("Tester")
                .userPoint(0L)
                .tier(Tiers.BRONZE)
                .build();

        user.updatePoint(5000L);
        assertEquals(Tiers.SILVER, user.getTier());

        user.updatePoint(12000L);
        assertEquals(Tiers.GOLD, user.getTier());

        user.updatePoint(80000L);
        assertEquals(Tiers.DIAMOND, user.getTier());

        user.updatePoint(200000L);
        assertEquals(Tiers.MASTER, user.getTier());
    }

    @Test
    public void testTierDowngrade() {
        Users user = Users.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .nickname("Tester")
                .userPoint(80000L)
                .tier(Tiers.DIAMOND)
                .build();

        user.updatePoint(20000L); // 포인트 감소
        assertEquals(Tiers.PLATINUM, user.getTier());

        user.updatePoint(500L); // 더 감소
        assertEquals(Tiers.BRONZE, user.getTier());
    }

}