package MathCaptain.weakness.domain.Record.dto.response;

import MathCaptain.weakness.domain.Record.entity.UserLog.RunningDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RunningLogResponse {

    private Long activityId;

    private Long duration;

    private String date;

    private Long distance;

    private String memo;

    private RunningLogResponse(Long activityId, Long duration, String date, Long distance, String memo) {
        this.activityId = activityId;
        this.duration = duration;
        this.date = date;
        this.distance = distance;
        this.memo = memo;
    }

    public static RunningLogResponse of(RunningDetail runningDetail) {
        return new RunningLogResponse(runningDetail.getActivityId(), runningDetail.getDuration(), runningDetail.getDate().toString(), runningDetail.getDistance(), runningDetail.getMemo());
    }
}
