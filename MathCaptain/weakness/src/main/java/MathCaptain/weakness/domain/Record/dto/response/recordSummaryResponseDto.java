package MathCaptain.weakness.domain.Record.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class recordSummaryResponseDto {

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
    private int personalDailyGoal;

    // 사용자가 설정한 주간 목표
    private int personalWeeklyGoal;
}
