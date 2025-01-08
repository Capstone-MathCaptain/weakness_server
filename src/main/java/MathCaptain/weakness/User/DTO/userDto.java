package MathCaptain.weakness.User.DTO;

import lombok.*;

@Data
@Builder(access = AccessLevel.PUBLIC)
public class userDto {

    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phoneNumber;

}

