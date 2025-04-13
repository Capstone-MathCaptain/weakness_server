package MathCaptain.weakness.domain.Record.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StudyLogEnrollRequest extends ActivityLogEnrollRequest{

    private Long activityTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String subject;

    private String memo;
}
