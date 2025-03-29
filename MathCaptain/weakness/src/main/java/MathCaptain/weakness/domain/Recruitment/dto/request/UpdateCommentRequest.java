package MathCaptain.weakness.domain.Recruitment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCommentRequest {

    @NotNull(message = "댓글을 작성해주세요")
    @NotEmpty(message = "댓글을 작성해주세요")
    private String content;
}
