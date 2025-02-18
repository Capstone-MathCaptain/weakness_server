package MathCaptain.weakness.Group.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupJoinResponseDto {

    private Long groupJoinId;

    private Long userId;

    private Long groupId;

    private String userNickname;

//    private int userPoint;

    private int personalDailyGoal;

    private int personalWeeklyGoal;
}
