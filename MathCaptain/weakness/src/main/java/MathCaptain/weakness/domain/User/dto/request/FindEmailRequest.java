package MathCaptain.weakness.domain.User.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindEmailRequest {

    private String userName;

    private String phoneNumber;
}
