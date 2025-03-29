package MathCaptain.weakness.domain.Record.dto.response;

import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordSummaryResponse {

    // 유저 이름
    private String userName;

    // 그룹 이름
    private String groupName;

    // 수행 시간
    private Long durationInMinutes;

    // 일간 목표 달성 여부
    private boolean dailyGoalAchieved;

    // 주간 목표 달성 여부
    private boolean weeklyGoalAchieved;

    // 일간 목표 달성까지 남은 시간 (분) (달성시 0)
    private Long remainingDailyGoalMinutes;

    // 주간 목표 달성까지 남은 일 수 (일) (달성시 0)
    private int remainingWeeklyGoalDays;

    // 사용자가 설정한 일간 목표
    private Long personalDailyGoal;

    // 사용자가 설정한 주간 목표
    private int personalWeeklyGoal;

    @Builder
    private RecordSummaryResponse(String userName, String groupName, Long durationInMinutes,
                                  boolean dailyGoalAchieved, boolean weeklyGoalAchieved,
                                  Long remainingDailyGoalMinutes, int remainingWeeklyGoalDays,
                                  Long personalDailyGoal, int personalWeeklyGoal) {
        this.userName = userName;
        this.groupName = groupName;
        this.durationInMinutes = durationInMinutes;
        this.dailyGoalAchieved = dailyGoalAchieved;
        this.weeklyGoalAchieved = weeklyGoalAchieved;
        this.remainingDailyGoalMinutes = remainingDailyGoalMinutes;
        this.remainingWeeklyGoalDays = remainingWeeklyGoalDays;
        this.personalDailyGoal = personalDailyGoal;
        this.personalWeeklyGoal = personalWeeklyGoal;
    }

    public static RecordSummaryResponse of(ActivityRecord record, RelationBetweenUserAndGroup relation) {
        return RecordSummaryResponse.builder()
                .userName(record.getUser().getName())
                .groupName(record.getGroup().getName())
                .durationInMinutes(record.getDurationInMinutes())
                .dailyGoalAchieved(record.isDailyGoalAchieved())
                .weeklyGoalAchieved(record.isWeeklyGoalAchieved())
                .remainingDailyGoalMinutes(relation.remainingDailyGoalMinutes())
                .remainingWeeklyGoalDays(relation.remainingWeeklyGoalDays())
                .personalDailyGoal(relation.getPersonalDailyGoal() * 60L)
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }
}
