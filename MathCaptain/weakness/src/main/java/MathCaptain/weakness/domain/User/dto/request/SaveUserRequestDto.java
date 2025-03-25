package MathCaptain.weakness.domain.User.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder(access = AccessLevel.PUBLIC)
public class SaveUserRequestDto {

    @Email
    @NotNull(message = "이메일은 필수입니다!")
    private String email;

    @NotNull(message = "비밀번호는 필수입니다!")
    @Size(min = 5, max = 20, message = "비밀번호는 최소 5글자 이상, 20글자 이하입니다.")
    private String password;

    @NotNull(message = "이름은 필수입니다!")
    @Size(min = 3, max = 15, message = "이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String name;

    @NotNull(message = "어플 내에서 사용하실 이름을 지정해주세요.")
    @Size(min = 3, max = 15, message = "별명은 최소 3글자 ~ 15글자 이하입니다.")
    private String nickname;

    @NotNull(message = "전화번호는 필수입니다!")
    @Size(min = 11, max = 13, message = "전화번호를 잘못입력하셨습니다! 다시 입력해주세요.")
    private String phoneNumber;

}

