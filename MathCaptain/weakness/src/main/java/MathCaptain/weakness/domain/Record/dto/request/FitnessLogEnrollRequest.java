package MathCaptain.weakness.domain.Record.dto.request;

import MathCaptain.weakness.domain.Record.entity.UserLog.ExerciseInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class FitnessLogEnrollRequest extends ActivityLogEnrollRequest {

    @NotNull
    private Long activityTime;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private List<ExerciseInfo> exerciseInfoList;
}
