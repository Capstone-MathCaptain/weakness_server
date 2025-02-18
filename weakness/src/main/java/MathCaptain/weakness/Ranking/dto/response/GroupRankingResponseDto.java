package MathCaptain.weakness.Ranking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupRankingResponseDto {
    private Long groupId;
    private String groupName;
    private Long totalGroupPoint;
    private int ranking;
}