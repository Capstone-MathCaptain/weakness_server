package MathCaptain.weakness.domain.Recruitment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitmentCreateResponseDto {

    private final Long groupId;

    private final String leaderName;

    private final String groupName;
}
