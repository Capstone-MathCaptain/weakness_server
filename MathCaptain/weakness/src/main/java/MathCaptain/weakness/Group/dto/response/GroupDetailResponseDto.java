package MathCaptain.weakness.Group.dto.response;

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

    private Long leaderId;

    private String leaderName;

    private int minDailyHours;

    private int minWeeklyDays;

    private Long groupPoint;

    private List<String> hashtags;

    private String groupImageUrl;

    private Map<DayOfWeek, Integer> weeklyGoalAchieve;

    private Long memberCount;
}
