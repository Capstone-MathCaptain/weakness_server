package MathCaptain.weakness.Ranking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalRankingResponseDto {
    private Long userId;
    private String nickname;
    private Long personalPoint;
    private int ranking;
}
