package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RELATION_BETWEEN_USER_AND_GROUP")
public class RelationBetweenUserAndGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member")
    private Users member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "joinGroup")
    private Group joinGroup;

    @Column(nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    @Range(min = 0, max = 24)
    private int personalDailyGoal;

    @Column(nullable = false)
    @Range(min = 0)
    private int personalWeeklyGoal;

    // 일간 인증 수행 시간
    @Range(min = 0, max = 1440)
    private Long personalDailyGoalAchieve;

    // 주간 인증 수행 시간
    @Range(min = 0, max = 7)
    private int personalWeeklyGoalAchieve;

    private Boolean isWeeklyGoalAchieved;

    private Boolean isDailyGoalAchieved;

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

        if (this.isWeeklyGoalAchieved == null) {
            this.isWeeklyGoalAchieved = false;
        }

        if (this.isDailyGoalAchieved == null) {
            this.isDailyGoalAchieved = false;
        }
        this.personalWeeklyGoalAchieve = 0;
        this.weeklyGoalAchieveStreak = 0;
        this.personalDailyGoalAchieve = 0L;

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

    // 주간 목표 달성 여부 초기화
    public void resetIsWeeklyGoalAchieved() {
        this.isWeeklyGoalAchieved = false;
    }

    // 일간 목표 달성 여부 초기화
    public void resetIsDailyGoalAchieved() {
        this.isDailyGoalAchieved = false;
    }

    // 주간 목표 달성 연속 횟수 초기화
    public void resetWeeklyGoalAchieveStreak() {
        this.weeklyGoalAchieveStreak = 0;
    }

    public boolean isWeeklyGoalAchieved() {
        return this.isWeeklyGoalAchieved;
    }

    public boolean isDailyGoalAchieved() {
        return this.isDailyGoalAchieved;
    }
}
