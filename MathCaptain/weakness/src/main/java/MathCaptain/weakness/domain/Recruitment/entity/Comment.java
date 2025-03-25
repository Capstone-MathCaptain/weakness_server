package MathCaptain.weakness.domain.Recruitment.entity;

import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.domain.User.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    //==수정==//
    public void updateContent(String content) {
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            this.lastModifiedTime = LocalDateTime.now();
        }
    }

    public void updateComment(UpdateCommentRequestDto requestDto) {
        updateContent(requestDto.getContent());
    }

    public Boolean isBelongToPost(Long recruitmentId) {
        return this.post.getPostId().equals(recruitmentId);
    }
}
