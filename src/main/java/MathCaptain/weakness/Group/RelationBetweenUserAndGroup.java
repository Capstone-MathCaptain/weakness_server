package MathCaptain.weakness.Group;

import ch.qos.logback.core.status.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class RelationBetweenUserAndGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long groupId;

    private Integer categoryId1;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private Float personalDailyGoal;

    @Column(nullable = false)
    private Float personalWeeklyGoal;

//    @Enumerated(EnumType.STRING)
//    private Status status; // Enum for status (e.g., ACTIVE, INACTIVE)
}
