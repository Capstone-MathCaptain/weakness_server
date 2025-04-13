package MathCaptain.weakness.domain.Record.dto.response;

import MathCaptain.weakness.domain.Record.entity.UserLog.ExerciseInfo;
import MathCaptain.weakness.domain.Record.entity.UserLog.FitnessDetail;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FitnessLogResponse {

    private Long activityId;

    private Long duration;

    private String date;

    private List<ExerciseInfo> exerciseInfoList;

    private FitnessLogResponse(Long activityId, Long duration, String date, List<ExerciseInfo> exerciseInfoList) {
        this.activityId = activityId;
        this.duration = duration;
        this.date = date;
        this.exerciseInfoList = exerciseInfoList;
    }

    public static FitnessLogResponse of(FitnessDetail fitnessDetail) {
        return new FitnessLogResponse(
                fitnessDetail.getActivityId(),
                fitnessDetail.getDuration(),
                fitnessDetail.getDate().toString(),
                fitnessDetail.getExerciseInfoList()
        );
    }
}
