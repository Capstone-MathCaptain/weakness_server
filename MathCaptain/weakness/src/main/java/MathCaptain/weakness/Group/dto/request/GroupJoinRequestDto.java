package MathCaptain.weakness.Group.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class GroupJoinRequestDto {

//    private Long groupId;

    private Long userId;

    @NotNull(message = "개인 일간 목표 설정은 필수입니다.")
    @Range(min = 1, max = 24, message = "개인 일일 목표 설정 오류!")
    private int personalDailyGoal;

    @NotNull(message = "개인 주간 목표 설정은 필수입니다.")
    @Range(min = 1, max = 7, message = "개인 주간 목표 설정 오류!")
    private int personalWeeklyGoal;
}
