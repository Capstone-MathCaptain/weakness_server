package MathCaptain.weakness.domain.User.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePwdDto {

    private String uuid;

    private String newPassword;

    private String confirmPassword;

}
