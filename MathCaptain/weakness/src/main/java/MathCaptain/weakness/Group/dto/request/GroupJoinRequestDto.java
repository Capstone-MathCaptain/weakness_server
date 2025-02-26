package MathCaptain.weakness.Group.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class GroupJoinRequestDto {

    private int personalDailyGoal;

    private int personalWeeklyGoal;
}
