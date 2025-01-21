package MathCaptain.weakness.Recruitment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentRequestDto {

        @NotNull
        @NotEmpty
        private Long authorId;

        @NotNull
        @NotEmpty(message = "댓글을 입력해주세요!")
        private String content;
}
