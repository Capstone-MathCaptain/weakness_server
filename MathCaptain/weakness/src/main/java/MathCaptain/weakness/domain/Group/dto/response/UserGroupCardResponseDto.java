package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.enums.GroupRole;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Map;

@Data
@Builder
public class UserGroupCardResponseDto {

    private Long groupId;

    private String groupName;

    private String groupImageUrl;

    private GroupRole groupRole;

    private int groupRanking;

    private Long groupPoint;

    private Map<DayOfWeek, Boolean> userAchieve;

    private int userDailyGoal;

    private int userWeeklyGoal;

}
