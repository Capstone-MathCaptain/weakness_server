package MathCaptain.weakness.domain.Group.dto.request;

import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@Builder
public class GroupCreateRequestDto {

    private long leaderId;

    @NotNull(message = "그룹 이름은 필수입니다.")
    @Size(min = 3, max = 15, message = "그룹 이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String groupName;

    @NotNull(message = "그룹 카테고리를 지정해주세요.")
    private CategoryStatus category;

    @Range(min = 1, max = 24, message = "최소 일일 시간 설정 오류!")
    private int minDailyHours;

    @Range(min = 1, max = 7, message = "최소 주간 일수 설정 오류!")
    private int minWeeklyDays;

    private Long groupPoint;

    private List<String> hashtags;

    private String groupImageUrl;

    // leader 개인 목표
    @Range(min = 1, max = 24, message = "개인 일일 목표 설정 오류!")
    private int personalDailyGoal;

    @Range(min = 1, max = 7, message = "개인 주간 목표 설정 오류!")
    private int personalWeeklyGoal;

}
