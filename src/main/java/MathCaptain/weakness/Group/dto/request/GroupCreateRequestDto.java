package MathCaptain.weakness.Group.dto.request;

import MathCaptain.weakness.Group.enums.CategoryStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupCreateRequestDto {

    private long leader_id;

    private String group_name;

    private CategoryStatus Category;

    private int min_daily_hours;

    private int min_weekly_days;

    private Long group_point;

    private List<String> hashtags;

    private Boolean disturb_mode;

    private String group_image_url;

    // leader 개인 목표
    private int personalDailyGoal;

    private int personalWeeklyGoal;

}
