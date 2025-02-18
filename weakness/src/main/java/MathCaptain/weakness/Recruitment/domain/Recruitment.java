package MathCaptain.weakness.Recruitment.domain;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Recruitment.enums.RecruitmentStatus;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
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

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @Column(nullable = false)
    private LocalDateTime postTime;

    private LocalDateTime lastModifiedTime;

    // 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.postTime = LocalDateTime.now();
        this.recruitmentStatus = RecruitmentStatus.RECRUITING;
        this.interestCount = 0L;
    }

    //== 수정 로직 ==//
    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedTime = LocalDateTime.now();
    }

    public void updateTitle(String title) {
        this.title = title;
        this.lastModifiedTime = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.lastModifiedTime = LocalDateTime.now();
    }

    public void updateRecruitmentStatus(RecruitmentStatus recruitmentStatus) {
        this.recruitmentStatus = recruitmentStatus;
        this.lastModifiedTime = LocalDateTime.now();
    }
}
