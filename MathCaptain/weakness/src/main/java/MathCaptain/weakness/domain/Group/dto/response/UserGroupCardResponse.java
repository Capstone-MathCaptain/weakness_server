package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.common.enums.GroupRole;
import lombok.*;

import java.time.DayOfWeek;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGroupCardResponse {

    private Long groupId;

    private String groupName;

    private String groupImageUrl;

    private GroupRole groupRole;

    private int groupRanking;

    private Long groupPoint;

    private Map<DayOfWeek, Boolean> userAchieve;

    private int userDailyGoal;

    private int userWeeklyGoal;

    @Builder
    private UserGroupCardResponse(Long groupId, String groupName, String groupImageUrl, GroupRole groupRole, int groupRanking, Long groupPoint, Map<DayOfWeek, Boolean> userAchieve, int userDailyGoal, int userWeeklyGoal) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupImageUrl = groupImageUrl;
        this.groupRole = groupRole;
        this.groupRanking = groupRanking;
        this.groupPoint = groupPoint;
        this.userAchieve = userAchieve;
        this.userDailyGoal = userDailyGoal;
        this.userWeeklyGoal = userWeeklyGoal;
    }

    public static UserGroupCardResponse of(Group group, RelationBetweenUserAndGroup relation, Map<DayOfWeek, Boolean> userAchieveInGroup) {
        return UserGroupCardResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .groupImageUrl(group.getGroupImageUrl())
                .groupRole(relation.getGroupRole())
                .groupRanking(group.getGroupRanking())
                .groupPoint(group.getGroupPoint())
                .userAchieve(userAchieveInGroup)
                .userDailyGoal(relation.getPersonalDailyGoal())
                .userWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }
}
