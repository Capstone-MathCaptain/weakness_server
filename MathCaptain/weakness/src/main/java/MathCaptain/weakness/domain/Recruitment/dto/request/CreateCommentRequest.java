package MathCaptain.weakness.domain.Recruitment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCommentRequest {

    @NotNull(message = "내용을 입력해주세요!")
    @NotEmpty(message = "내용을 입력해주세요!")
    private String content;

}
