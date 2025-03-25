package MathCaptain.weakness.domain.Record.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class recordEndRequestDto {

    // 수행 시간 (분)
    private Long activityTime;

}
