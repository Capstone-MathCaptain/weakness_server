package MathCaptain.weakness.domain.Ranking.dto.response;

import MathCaptain.weakness.domain.Group.entity.Group;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupRankingResponse {

    private Long groupId;

    private String groupName;

    private Long groupPoint;

    private int ranking;

    @Builder
    private GroupRankingResponse(Long groupId, String groupName, Long groupPoint, int ranking) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPoint = groupPoint;
        this.ranking = ranking;
    }

    public static GroupRankingResponse of(Group group) {
        return GroupRankingResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .groupPoint(group.getGroupPoint())
                .ranking(group.getGroupRanking())
                .build();
    }
}