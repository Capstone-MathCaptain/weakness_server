package MathCaptain.weakness.domain.User.dto.response;

import MathCaptain.weakness.domain.User.entity.Users;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class findEmailResponse {

    private String email;

    private findEmailResponse(String email) {
        this.email = email;
    }

    public static findEmailResponse of(Users user) {
        return new findEmailResponse(user.getEmail());
    }
}
