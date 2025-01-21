package MathCaptain.weakness.User.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindEmailRequestDto {

    @NotNull(message = "이름은 필수입니다!")
    @Size(min = 3, max = 15, message = "이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String userName;

    @NotNull(message = "전화번호는 필수입니다!")
    @Size(min = 11, max = 13, message = "전화번호를 잘못입력하셨습니다! 다시 입력해주세요.")
    private String phoneNumber;
}
