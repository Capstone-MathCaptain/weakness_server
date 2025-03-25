package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
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

    private Long groupPoint;

    private int groupRanking;

    private List<String> hashtags;

    private Boolean disturb_mode;

    private LocalDate created_date;

    private String groupImageUrl;
}
