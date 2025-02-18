package MathCaptain.weakness.Challenge;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    private Long groupId;

    @Column(nullable = false, length = 500)
    private String challengeContent;
}
