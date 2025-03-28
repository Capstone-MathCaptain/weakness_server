package MathCaptain.weakness.domain.User.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveUserRequest {

    @Email
    private String email;

    @Size(min = 5, max = 20, message = "비밀번호는 최소 5글자 이상, 20글자 이하입니다.")
    private String password;

    @Size(min = 3, max = 15, message = "이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String name;

    @Size(min = 3, max = 15, message = "별명은 최소 3글자 ~ 15글자 이하입니다.")
    private String nickname;

    @Size(min = 11, max = 13, message = "전화번호를 잘못입력하셨습니다! 다시 입력해주세요.")
    private String phoneNumber;

    private SaveUserRequest(String email, String password, String name, String nickname, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    public static SaveUserRequest of(String email, String password, String name,
                                     String nickname, String phoneNumber) {
        return new SaveUserRequest(email, password, name, nickname, phoneNumber);
    }

}

