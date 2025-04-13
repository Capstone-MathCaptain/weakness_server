package MathCaptain.weakness.domain.Record.entity.UserLog;

import MathCaptain.weakness.domain.Record.dto.request.FitnessLogEnrollRequest;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class FitnessDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long activityId;

    private Long duration;

    private LocalDate date;

    @ElementCollection
    private List<ExerciseInfo> exerciseInfoList;

    private FitnessDetail(Long activityId, Long duration, LocalDate date, List<ExerciseInfo> exerciseInfoList) {
        this.activityId = activityId;
        this.duration = duration;
        this.date = date;
        this.exerciseInfoList = exerciseInfoList;
    }

    public static FitnessDetail of(ActivityRecord record, FitnessLogEnrollRequest request) {
        return new FitnessDetail(record.getId(), record.getDurationInMinutes(), record.getEndTime().toLocalDate(), request.getExerciseInfoList());
    }
}
