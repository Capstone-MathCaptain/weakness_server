package MathCaptain.weakness.Ranking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankingId;

    private Integer categoryId1;

    private Integer categoryId2;

    @Column(nullable = false)
    private Float weeklyAchievement;

    @Column(nullable = false)
    private Integer streakWeeks;

    @Column(nullable = false)
    private Integer totalExperience;
}
