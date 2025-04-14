package MathCaptain.weakness.domain.Record.entity;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Record.dto.request.ActivityLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.request.RecordEndRequest;
import MathCaptain.weakness.domain.User.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Getter
@Entity(name = "activity_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ActivityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime startTime; // 인증 시작 시간

    private LocalDateTime endTime; // 인증 종료 시간

    @Range(min = 0)
    @Column(nullable = false)
    private Long durationInMinutes; // 활동 시간 (분)

    private boolean dailyGoalAchieved; // 일간 목표 달성 여부

    private boolean weeklyGoalAchieved; // 주간 목표 달성 여부

    @Column(nullable = false)
    private DayOfWeek dayOfWeek; // 요일

    @Builder
    private ActivityRecord(Users user, Group group, LocalDateTime startTime, LocalDateTime endTime, Long durationInMinutes,
                          boolean dailyGoalAchieved, boolean weeklyGoalAchieved, DayOfWeek dayOfWeek) {
        this.user = user;
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationInMinutes = durationInMinutes;
        this.dailyGoalAchieved = dailyGoalAchieved;
        this.weeklyGoalAchieved = weeklyGoalAchieved;
        this.dayOfWeek = dayOfWeek;
    }

    public static ActivityRecord of(
            RelationBetweenUserAndGroup relation,
            DayOfWeek dayOfWeek, LocalDateTime startTime, LocalDateTime endTime, Long duration) {
        return ActivityRecord.builder()
                .user(relation.getMember())
                .group(relation.getGroup())
                .startTime(startTime)
                .endTime(endTime)
                .durationInMinutes(duration)
                .dayOfWeek(dayOfWeek)
                .build();
    }

    public static ActivityRecord of(
            RelationBetweenUserAndGroup relation, ActivityLogEnrollRequest request) {
        return ActivityRecord.builder()
                .user(relation.getMember())
                .group(relation.getGroup())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationInMinutes(request.getActivityTime())
                .dayOfWeek(request.getStartTime().getDayOfWeek())
                .build();
    }

    public static ActivityRecord of(Users user, Group group, LocalDateTime startTime, LocalDateTime endTime,
                                    Long activityTime, DayOfWeek dayOfWeek) {
        return ActivityRecord.builder()
                .user(user)
                .group(group)
                .startTime(startTime)
                .endTime(endTime)
                .durationInMinutes(activityTime)
                .dayOfWeek(dayOfWeek)
                .build();
    }

    public void updateDailyGoalAchieved(boolean dailyGoalAchieved) {
        this.dailyGoalAchieved = dailyGoalAchieved;
    }

    public void updateWeeklyGoalAchieved(boolean weeklyGoalAchieved) {
        this.weeklyGoalAchieved = weeklyGoalAchieved;
    }
}
