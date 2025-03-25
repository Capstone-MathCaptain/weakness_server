package MathCaptain.weakness.domain.Group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupSearchRequestDto {

    @NotNull(message = "그룹 이름을 입력해주세요.")
    @NotBlank(message = "그룹 이름을 입력해주세요.")
    private String groupName;
}
