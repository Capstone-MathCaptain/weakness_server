package MathCaptain.weakness.domain.Recruitment.entity;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateRecruitmentRequestDto;
import MathCaptain.weakness.domain.Recruitment.enums.RecruitmentStatus;
import MathCaptain.weakness.domain.User.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private Users author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitGroup")
    private Group recruitGroup;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CategoryStatus category;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus recruitmentStatus;

    // 좋아요 개수
    private Long interestCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @Column(nullable = false)
    private LocalDateTime postTime;

    private LocalDateTime lastModifiedTime;

    // 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.postTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
        this.recruitmentStatus = RecruitmentStatus.RECRUITING;
        this.interestCount = 0L;
    }

    //== 수정 로직 ==//
    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedTime = LocalDateTime.now();
    }

    public void updateTitle(String title) {
        if (title != null && !title.equals(this.title))
        {
            this.title = title;
            updateLastModifiedTime();
        }
    }

    public void updateContent(String content) {
        if (content != null && !content.equals(this.content))
        {
            this.content = content;
            updateLastModifiedTime();
        }
    }

    public void updateRecruitmentStatus(RecruitmentStatus recruitmentStatus) {
        if (recruitmentStatus != null && !recruitmentStatus.equals(this.recruitmentStatus))
        {
            this.recruitmentStatus = recruitmentStatus;
            updateLastModifiedTime();
        }
    }

    private void updateLastModifiedTime() {
        this.lastModifiedTime = LocalDateTime.now();
    }

    public void updateRecruitment(UpdateRecruitmentRequestDto requestDto) {
        updateTitle(requestDto.getTitle());
        updateContent(requestDto.getContent());
        updateRecruitmentStatus(requestDto.getRecruitmentStatus());
    }
}
