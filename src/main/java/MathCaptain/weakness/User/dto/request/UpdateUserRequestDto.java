package MathCaptain.weakness.User.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    @Email
    @NotNull(message = "이메일은 필수입니다!")
    private String email;

    @Size(min = 3, max = 15, message = "이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String name;

    @Size(min = 3, max = 15, message = "별명은 최소 3글자 ~ 15글자 이하입니다.")
    private String nickname;

    @Size(min = 11, max = 13, message = "전화번호를 잘못입력하셨습니다! 다시 입력해주세요.")
    private String phoneNumber;

}
