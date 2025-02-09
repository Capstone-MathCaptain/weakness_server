package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.enums.RequestStatus;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter
@Builder
@Table(name = "GROUP_JOIN")
@NoArgsConstructor
@AllArgsConstructor
public class GroupJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupJoinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private RequestStatus requestStatus;

    @Column(nullable = false)
    @Range(min = 0, max = 24)
    private int personalDailyGoal;

    @Column(nullable = false)
    @Range(min = 0)
    private int personalWeeklyGoal;

    public void updateRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
