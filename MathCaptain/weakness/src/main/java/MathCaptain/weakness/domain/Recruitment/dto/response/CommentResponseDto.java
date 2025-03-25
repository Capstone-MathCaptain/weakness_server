package MathCaptain.weakness.domain.Recruitment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDto {

    private Long commentId;

    private String authorName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
