package MathCaptain.weakness.domain.Record.dto.response;

import MathCaptain.weakness.domain.Record.entity.UserLog.StudyDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyLogResponse {

    private Long activityId;

    private Long duration;

    private String subject;

    private String date;

    private String memo;

    private StudyLogResponse(Long activityId, Long duration, String subject, String date, String memo) {
        this.activityId = activityId;
        this.subject = subject;
        this.duration = duration;
        this.date = date;
        this.memo = memo;
    }

    public static StudyLogResponse of(StudyDetail studyDetail) {
        return new StudyLogResponse(studyDetail.getActivityId(), studyDetail.getDuration(), studyDetail.getSubject(), studyDetail.getDate().toString(), studyDetail.getMemo());
    }
}
