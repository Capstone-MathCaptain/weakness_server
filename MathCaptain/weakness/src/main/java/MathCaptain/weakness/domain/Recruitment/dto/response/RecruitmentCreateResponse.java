package MathCaptain.weakness.domain.Recruitment.dto.response;

import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentCreateResponse {

    private Long groupId;

    private String leaderName;

    private String groupName;

    private RecruitmentCreateResponse(Long groupId, String leaderName, String groupName) {
        this.groupId = groupId;
        this.leaderName = leaderName;
        this.groupName = groupName;
    }

    public static RecruitmentCreateResponse of(RelationBetweenUserAndGroup relation) {
        return new RecruitmentCreateResponse(relation.getGroup().getId(), relation.getGroup().getName(), relation.getMember().getName());
    }
}
