package MathCaptain.weakness.domain.Recruitment.entity;

import MathCaptain.weakness.domain.Recruitment.dto.request.CreateCommentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateCommentRequest;
import MathCaptain.weakness.domain.User.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "COMMENT")
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post")
    private Recruitment post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private Users author;

    @Column(nullable = false, length = 1000)
    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime commentTime;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedTime;

    @Builder
    private Comment(Recruitment post, Users author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.commentTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
    }

    public static Comment of(Recruitment recruitment, Users author, CreateCommentRequest createCommentRequest) {
        return new Comment(recruitment, author, createCommentRequest.getContent());
    }

    public static Comment of(Recruitment recruitment, Users author, String content) {
        return new Comment(recruitment, author, content);
    }

    public void updateComment(UpdateCommentRequest updateRequest) {
        this.content = updateRequest.getContent();
        this.lastModifiedTime = LocalDateTime.now();
    }

    public Boolean isBelongToPost(Long recruitmentId) {
        return this.post.getPostId().equals(recruitmentId);
    }
}
