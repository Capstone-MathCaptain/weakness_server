package MathCaptain.weakness.Recruitment.domain;

import MathCaptain.weakness.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Column(nullable = false)
    private LocalDateTime commentTime;

    @Column(nullable = false)
    private LocalDateTime lastModifiedTime;

    // 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.commentTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
    }

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
