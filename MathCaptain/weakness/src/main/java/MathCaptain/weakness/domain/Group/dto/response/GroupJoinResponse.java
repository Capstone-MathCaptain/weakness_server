package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupJoinResponse {

    private Long groupJoinId;

    private Long userId;

    private Long groupId;

    private String userNickname;

    private Long userPoint;

    private int personalDailyGoal;

    private int personalWeeklyGoal;

    @Builder
    private GroupJoinResponse(Long groupJoinId, Long userId, Long groupId, String userNickname,
                              Long userPoint, int personalDailyGoal, int personalWeeklyGoal) {
        this.groupJoinId = groupJoinId;
        this.userId = userId;
        this.groupId = groupId;
        this.userNickname = userNickname;
        this.userPoint = userPoint;
        this.personalDailyGoal = personalDailyGoal;
        this.personalWeeklyGoal = personalWeeklyGoal;
    }

    public static GroupJoinResponse of(RelationBetweenUserAndGroup relation) {
        return GroupJoinResponse.builder()
                .groupJoinId(relation.getGroup().getId())
                .userId(relation.getMember().getUserId())
                .userNickname(relation.getMember().getNickname())
                .userPoint(relation.getMember().getUserPoint())
                .personalDailyGoal(relation.getPersonalDailyGoal())
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }

}
