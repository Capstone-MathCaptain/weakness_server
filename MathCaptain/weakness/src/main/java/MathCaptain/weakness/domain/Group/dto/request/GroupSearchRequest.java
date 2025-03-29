package MathCaptain.weakness.domain.Group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupSearchRequest {

    @NotNull(message = "그룹 이름을 입력해주세요.")
    @NotBlank(message = "그룹 이름을 입력해주세요.")
    private String groupName;
}
