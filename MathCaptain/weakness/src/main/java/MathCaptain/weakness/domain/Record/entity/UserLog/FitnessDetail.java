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
@Table(name = "fitness_detail")
public class FitnessDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long activityId;

    private Long duration;

    private LocalDate date;

    @ElementCollection
    @CollectionTable(
            name = "fitnessdetail_exerciseinfolist",
            joinColumns = @JoinColumn(name = "fitness_detail_id")
    )
    private List<ExerciseInfo> exerciseInfoList;

    private FitnessDetail(Long activityId, Long userId, Long duration, LocalDate date, List<ExerciseInfo> exerciseInfoList) {
        this.activityId = activityId;
        this.userId = userId;
        this.duration = duration;
        this.date = date;
        this.exerciseInfoList = exerciseInfoList;
    }

    public static FitnessDetail of(ActivityRecord record, FitnessLogEnrollRequest request) {
        return new FitnessDetail(record.getId(), record.getUser().getUserId(), record.getDurationInMinutes(), record.getEndTime().toLocalDate(), request.getExerciseInfoList());
    }

    public static FitnessDetail of(ActivityRecord record, List<ExerciseInfo> exerciseInfoList) {
        return new FitnessDetail(record.getId(), record.getUser().getUserId(), record.getDurationInMinutes(), record.getEndTime().toLocalDate(), exerciseInfoList);
    }
}
