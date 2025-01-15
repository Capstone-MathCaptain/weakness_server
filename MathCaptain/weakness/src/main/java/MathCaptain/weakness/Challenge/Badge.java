package MathCaptain.weakness.Challenge;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badgeId;

    @Column(nullable = false, length = 100)
    private String badgeName;
}
