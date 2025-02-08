package MathCaptain.weakness.Record.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class recordStartResponseDto {

    private Long recordId;

    private Long userDailyGoal;

}
