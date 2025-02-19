package MathCaptain.weakness.Group.dto.response;

import MathCaptain.weakness.Group.enums.CategoryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GroupResponseDto {

    private Long groupId;

    private Long leaderId;

    private String leaderName;

    private String groupName;

    private CategoryStatus category;

    private int minDailyHours;

    private int minWeeklyDays;

    private Long group_point;

    private List<String> hashtags;

    private Boolean disturb_mode;

    private LocalDate created_date;

    private String groupImageUrl;
}
