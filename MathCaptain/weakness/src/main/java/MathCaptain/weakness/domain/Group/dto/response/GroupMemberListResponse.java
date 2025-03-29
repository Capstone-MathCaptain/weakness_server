package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.enums.GroupRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMemberListResponse {

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

    private Boolean isWeeklyGoalAchieved;

    @Builder
    private GroupMemberListResponse(Long userId, String userName, String userImageUrl,
                                    GroupRole userRole, Long userPoint, int userWeeklyGoal, int userDailyGoal,
                                    int currentProgress, Boolean isWeeklyGoalAchieved) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.userRole = userRole;
        this.userPoint = userPoint;
        this.userWeeklyGoal = userWeeklyGoal;
        this.userDailyGoal = userDailyGoal;
        this.currentProgress = currentProgress;
        this.isWeeklyGoalAchieved = isWeeklyGoalAchieved;
    }

    public static GroupMemberListResponse of(RelationBetweenUserAndGroup relation, Integer currentProgress) {
        return GroupMemberListResponse.builder()
                .userId(relation.getMember().getUserId())
                .userName(relation.getMember().getName())
                .userRole(relation.getGroupRole())
                .userPoint(relation.getMember().getUserPoint())
                .userWeeklyGoal(relation.getPersonalWeeklyGoal())
                .userDailyGoal(relation.getPersonalDailyGoal())
                .isWeeklyGoalAchieved(relation.isWeeklyGoalAchieved())
                .currentProgress(currentProgress)
                .build();
    }
}
