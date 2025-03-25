package MathCaptain.weakness.domain.User.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindPwdRequestDto {

    @Email
    @NotNull(message = "이메일은 필수입니다!")
    private String email;

    @NotNull(message = "이름은 필수입니다!")
    @Size(min = 3, max = 15, message = "이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String name;
}
