package MathCaptain.weakness.Recruitment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentRequestDto {

        private Long authorId;

        private String content;
}
