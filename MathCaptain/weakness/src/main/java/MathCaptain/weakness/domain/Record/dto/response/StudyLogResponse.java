package MathCaptain.weakness.domain.Record.dto.response;

import MathCaptain.weakness.domain.Record.entity.UserLog.StudyDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyLogResponse {

    private Long activityId;

    private String subject;

    private String date;

    private String memo;

    private StudyLogResponse(Long activityId, String subject, String date, String memo) {
        this.activityId = activityId;
        this.subject = subject;
        this.date = date;
        this.memo = memo;
    }

    public static StudyLogResponse of(StudyDetail studyDetail) {
        return new StudyLogResponse(studyDetail.getActivityId(), studyDetail.getSubject(), studyDetail.getDate().toString(), studyDetail.getMemo());
    }
}
