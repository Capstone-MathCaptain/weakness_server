package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class GroupDetailResponseDto {

    private Long groupId;

    private String groupName;

    private CategoryStatus category;

    private Long leaderId;

    private String leaderName;

    private int minDailyHours;

    private int minWeeklyDays;

    private Long groupPoint;

    private int groupRanking;

    private List<String> hashtags;

    private String groupImageUrl;

    private Map<DayOfWeek, Integer> weeklyGoalAchieve;

    private Integer totalWeeklyGoalCount;

    private Long memberCount;
}
