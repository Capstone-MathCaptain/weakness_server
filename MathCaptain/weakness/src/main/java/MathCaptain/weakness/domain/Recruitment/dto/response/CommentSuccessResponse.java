package MathCaptain.weakness.domain.Recruitment.dto.response;

import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentSuccessResponse {

    private Long recruitmentId;

    private Long commentId;

    private CommentSuccessResponse(Long recruitmentId, Long commentId) {
        this.recruitmentId = recruitmentId;
        this.commentId = commentId;
    }

    public static CommentSuccessResponse of(Long recruitmentId, Long commentId) {
        return new CommentSuccessResponse(recruitmentId, commentId);
    }

    public static CommentSuccessResponse of(Long recruitmentId, Comment comment) {
        return new CommentSuccessResponse(recruitmentId, comment.getCommentId());
    }
}
