package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.enums.GroupRole;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelationResponse {

    private Long id;

    private UserResponse member;

    private GroupResponse group;

    private GroupRole groupRole;

    private LocalDate joinDate;

    private int personalDailyGoal;

    private int personalWeeklyGoal;

    @Builder
    private RelationResponse(Long id, UserResponse member, GroupResponse group, GroupRole groupRole,
                             LocalDate joinDate, int personalDailyGoal, int personalWeeklyGoal) {
        this.id = id;
        this.member = member;
        this.group = group;
        this.groupRole = groupRole;
        this.joinDate = joinDate;
        this.personalDailyGoal = personalDailyGoal;
        this.personalWeeklyGoal = personalWeeklyGoal;
    }

    public static RelationResponse of(RelationBetweenUserAndGroup relation, UserResponse userResponse, GroupResponse groupResponse) {
        return RelationResponse.builder()
                .id(relation.getId())
                .member(userResponse)
                .groupRole(relation.getGroupRole())
                .group(groupResponse)
                .joinDate(relation.getJoinDate())
                .personalDailyGoal(relation.getPersonalDailyGoal())
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }


}
