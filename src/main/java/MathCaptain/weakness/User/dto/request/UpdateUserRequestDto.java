package MathCaptain.weakness.User.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    private String email;
    private String name;
    private String nickname;
    private String phoneNumber;

}
