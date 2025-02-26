package MathCaptain.weakness.Group.dto.response;

import MathCaptain.weakness.Group.enums.GroupRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberListResponseDto {

    @NotNull
    private Long userId;

    @NotNull
    private String userName;

    private String userImageUrl;

    @NotNull
    private GroupRole userRole;

    @NotNull
    private Long userPoint;

    @NotNull
    private int userWeeklyGoal;

    @NotNull
    private int userDailyGoal;

    private int currentProgress;

    private Boolean isAchieveWeeklyGoal;
}
