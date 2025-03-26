package MathCaptain.weakness.domain.Record.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class recordEndRequestDto {

    // 수행 시간 (분)
    private Long activityTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
