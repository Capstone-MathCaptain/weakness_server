package MathCaptain.weakness.Record.domain;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime startTime; // 인증 시작 시간

    private LocalDateTime endTime; // 인증 종료 시간

    private long durationInMinutes; // 활동 시간 (분)

    private boolean dailyGoalAchieved; // 일간 목표 달성 여부

    private boolean weeklyGoalAchieved; // 주간 목표 달성 여부

    private DayOfWeek dayOfWeek; // 요일

    @PrePersist
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationInMinutes = Duration.between(startTime, endTime).toMinutes();
        }
    }

    public void updateEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        calculateDuration();
    }

    public void updateDailyGoalAchieved(boolean dailyGoalAchieved) {
        this.dailyGoalAchieved = dailyGoalAchieved;
    }

    public void updateWeeklyGoalAchieved(boolean weeklyGoalAchieved) {
        this.weeklyGoalAchieved = weeklyGoalAchieved;
    }

    public void updateDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void updateDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
