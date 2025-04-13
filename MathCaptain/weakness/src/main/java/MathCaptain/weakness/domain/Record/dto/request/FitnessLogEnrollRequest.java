package MathCaptain.weakness.domain.Record.dto.request;

import MathCaptain.weakness.domain.Record.entity.UserLog.ExerciseInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class FitnessLogEnrollRequest extends ActivityLogEnrollRequest {

    private Long activityTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<ExerciseInfo> exerciseInfoList;
}
