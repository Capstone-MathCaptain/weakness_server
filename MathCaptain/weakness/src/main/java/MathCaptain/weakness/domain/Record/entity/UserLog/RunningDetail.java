package MathCaptain.weakness.domain.Record.entity.UserLog;

import MathCaptain.weakness.domain.Record.dto.request.RunningLogEnrollRequest;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "running_detail")
public class RunningDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private Long activityId;

    private Long duration;

    private Long distance;

    private LocalDate date;

    private String memo;

    private RunningDetail(Long activityId, Long userId, Long duration, Long distance, LocalDate date, String memo) {
        this.activityId = activityId;
        this.userId = userId;
        this.duration = duration;
        this.distance = distance;
        this.date = date;
        this.memo = memo;
    }

    public static RunningDetail of(ActivityRecord record, RunningLogEnrollRequest request) {
        return new RunningDetail(record.getId(), record.getUser().getUserId(), record.getDurationInMinutes(), request.getDistance(), record.getEndTime().toLocalDate() ,request.getMemo());
    }

    public static RunningDetail of(ActivityRecord record, Long distance, String memo) {
        return new RunningDetail(record.getId(), record.getUser().getUserId(), record.getDurationInMinutes(), distance, record.getEndTime().toLocalDate(), memo);
    }
}
