package MathCaptain.weakness.Ranking.domain;

import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PersonalRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private Long personalPoint;

    public PersonalRanking(Users user, Long personalPoint) {
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
