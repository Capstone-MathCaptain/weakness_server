package MathCaptain.weakness.Group.dto.request;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GroupUpdateRequestDto {

    private String groupName;

    private int min_daily_hours;

    private int min_weekly_days;

    private List<String> hashtags;

    private String group_image_url;
}
