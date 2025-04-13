package MathCaptain.weakness.domain.Record.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RunningLogEnrollRequest extends ActivityLogEnrollRequest {

    private Long activityTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long distance;

    private String memo;
}
