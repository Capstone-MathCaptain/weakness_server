package MathCaptain.weakness.User.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private Long userId;

    private String email;

    private String name;

    private String nickname;

    private String phoneNumber;
}
