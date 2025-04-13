package MathCaptain.weakness.domain.Record.entity.UserLog;

import MathCaptain.weakness.domain.Record.dto.request.StudyLogEnrollRequest;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class StudyDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long activityId;

    private Long duration;

    private LocalDate date;

    private String subject;

    private String memo;

    private StudyDetail(Long activityId, String subject, Long duration, LocalDate date, String memo) {
        this.activityId = activityId;
        this.subject = subject;
        this.duration = duration;
        this.date = date;
        this.memo = memo;
    }

    public static StudyDetail of(ActivityRecord record, StudyLogEnrollRequest request) {
        return new StudyDetail(record.getId(), request.getSubject(), record.getDurationInMinutes(), record.getEndTime().toLocalDate(),request.getMemo());
    }
}
