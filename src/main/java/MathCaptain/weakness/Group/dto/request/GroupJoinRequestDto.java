package MathCaptain.weakness.Group.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupJoinRequestDto {

//    private Long groupId;

    private Long userId;

    private int personalDailyGoal;

    private int personalWeeklyGoal;
}
