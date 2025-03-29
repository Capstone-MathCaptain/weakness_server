package MathCaptain.weakness.domain.Group.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupJoinRequest {

    private int personalDailyGoal;

    private int personalWeeklyGoal;

    private GroupJoinRequest(int personalDailyGoal, int personalWeeklyGoal) {
        this.personalDailyGoal = personalDailyGoal;
        this.personalWeeklyGoal = personalWeeklyGoal;
    }

    public static GroupJoinRequest of(GroupCreateRequest groupCreateRequest) {
        return new GroupJoinRequest(groupCreateRequest.getPersonalDailyGoal(), groupCreateRequest.getPersonalWeeklyGoal());
    }
}
