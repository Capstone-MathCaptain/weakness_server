package MathCaptain.weakness.domain.Record.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecordEndRequest {

    // 수행 시간 (분)
    private Long activityTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
