package MathCaptain.weakness.Group.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupUpdateRequestDto {

    private String groupName;

    private int min_daily_hours;

    private int min_weekly_days;

    private List<String> hashtags;

    private String group_image_url;
}
