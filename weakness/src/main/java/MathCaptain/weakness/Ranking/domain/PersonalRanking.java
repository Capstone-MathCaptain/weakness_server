package MathCaptain.weakness.Ranking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import MathCaptain.weakness.User; // 올바른 User 엔티티를 import

@Getter
@NoArgsConstructor
@Entity
public class PersonalRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long personalPoint;

    public PersonalRanking(User user, Long personalPoint) {
        this.user = user;
        this.personalPoint = personalPoint;
    }

    // 사용자 정보 반환 메서드 추가
    public Long getUserId() {
        return user != null ? user.getUserId() : null;
    }

    public String getNickname() {
        return user != null ? user.getNickname() : "Unknown";
    }
}
