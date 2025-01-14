package MathCaptain.weakness.User.dto.request;

import lombok.*;

@Data
@Builder(access = AccessLevel.PUBLIC)
public class SaveUserRequestDto {

    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phoneNumber;

}

