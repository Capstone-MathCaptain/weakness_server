package MathCaptain.weakness.Group.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupUpdateRequestDto {

    @NotNull(message = "그룹 이름은 필수입니다.")
    @Size(min = 3, max = 15, message = "그룹 이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String groupName;

    @Range(min = 1, max = 24, message = "최소 일일 시간 설정 오류!")
    private int minDailyHours;

    @Range(min = 1, max = 7, message = "최소 주간 일수 설정 오류!")
    private int minWeeklyDays;

    private List<String> hashtags;

    private String groupImageUrl;
}
