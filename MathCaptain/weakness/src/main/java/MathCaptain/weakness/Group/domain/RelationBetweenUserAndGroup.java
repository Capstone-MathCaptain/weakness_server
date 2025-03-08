package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.enums.RequestStatus;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "RELATION_BETWEEN_USER_AND_GROUP")
public class RelationBetweenUserAndGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member", referencedColumnName = "user_id")
    private Users member;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "members")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @Column(nullable = false)
    private LocalDate joinDate;

    private RequestStatus requestStatus;

    @Column(nullable = false)
    @Range(min = 0, max = 24)
    private int personalDailyGoal;

    @Column(nullable = false)
    @Range(min = 0)
    private int personalWeeklyGoal;

    // 일간 인증 수행 시간
    @Range(min = 0, max = 1440)
    private Long personalDailyGoalAchieve;

    // 주간 인증 수행 일수
    @Range(min = 0, max = 7)
    private int personalWeeklyGoalAchieve;

    // 주간 목표 달성 연속 횟수
    @Range(min = 0)
    private int weeklyGoalAchieveStreak;

    // 기본값 설정
    @PrePersist
    protected void onPrePersist() {

        if (this.groupRole == null) {
            this.groupRole = GroupRole.MEMBER; // 기본값 설정
        }
        if (this.joinDate == null) {
            this.joinDate = LocalDate.now(); // joinDate의 기본값 설정 (필요 시)
        }

        this.requestStatus = RequestStatus.WAITING;

        this.personalWeeklyGoalAchieve = 0;
        this.weeklyGoalAchieveStreak = 0;
        this.personalDailyGoalAchieve = 0L;

    }

    public void subtractPoint(Long point) {
        this.member.subtractPoint(point);
        this.group.subtractPoint(point);
    }

    public boolean isDailyGoalAchieved() {
        return this.personalDailyGoalAchieve >= this.personalDailyGoal;
    }

    public boolean isWeeklyGoalAchieved() {
        return this.personalWeeklyGoalAchieve >= this.personalWeeklyGoal;
    }

    // 일간 목표 업데이트
    public void updatePersonalDailyGoalAchieved(Long personalDailyGoalAchieved) {
        this.personalDailyGoalAchieve = personalDailyGoalAchieved;
    }

    // 주간 목표 업데이트
    public void updatePersonalWeeklyGoalAchieved(int personalWeeklyGoalAchieve) {
        this.personalWeeklyGoalAchieve = personalWeeklyGoalAchieve;
    }

    // 주간 목표 달성 연속 횟수 업데이트
    public void updateWeeklyGoalAchieveStreak(int weeklyGoalAchieveStreak) {
        this.weeklyGoalAchieveStreak = weeklyGoalAchieveStreak;
    }

    // 일간 목표 초기화
    public void resetPersonalDailyGoalAchieve() {
        this.personalDailyGoalAchieve = (Long) 0L;
    }

    // 주간 목표 초기화
    public void resetPersonalWeeklyGoalAchieve() {
        this.personalWeeklyGoalAchieve = 0;
    }

    // 주간 목표 달성 연속 횟수 초기화
    public void resetWeeklyGoalAchieveStreak() {
        this.weeklyGoalAchieveStreak = 0;
    }

    public void updateRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

}
