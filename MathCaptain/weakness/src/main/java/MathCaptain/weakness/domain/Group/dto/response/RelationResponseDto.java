package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.enums.GroupRole;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RelationResponseDto {

    private Long id;

    private UserResponse member;

    private GroupResponseDto group;

    private GroupRole groupRole;

    private LocalDate joinDate;

    private int personalDailyGoal;

    private int personalWeeklyGoal;
}
