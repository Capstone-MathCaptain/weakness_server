package MathCaptain.weakness.domain.Recruitment.dto.response;

import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponse {

    private Long commentId;

    private Long authorId;

    private String authorName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private CommentResponse(Long commentId, Long authorId, String authorName, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .authorId(comment.getAuthor().getUserId())
                .authorName(comment.getAuthor().getName())
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .createdAt(comment.getCommentTime())
                .updatedAt(comment.getLastModifiedTime())
                .build();
    }
}
