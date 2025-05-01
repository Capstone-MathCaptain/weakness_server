package MathCaptain.weakness.domain.Recruitment.entity;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.common.enums.CategoryStatus;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateRecruitmentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateRecruitmentRequest;
import MathCaptain.weakness.domain.common.enums.RecruitmentStatus;
import MathCaptain.weakness.domain.User.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @CreatedDate
    private LocalDateTime postTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedTime;

    @Builder
    private Recruitment(Users author, Group recruitGroup, CategoryStatus category, String title, String content, List<Comment> comments) {
        this.author = author;
        this.recruitGroup = recruitGroup;
        this.category = category;
        this.title = title;
        this.content = content;
        this.recruitmentStatus = RecruitmentStatus.RECRUITING;
        this.interestCount = 0L;
        this.comments = comments;
        this.postTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
    }

    public static Recruitment of(Users author, Group group, CreateRecruitmentRequest createRecruitmentRequest) {
        return Recruitment.builder()
                .author(author)
                .recruitGroup(group)
                .title(createRecruitmentRequest.getTitle())
                .content(createRecruitmentRequest.getContent())
                .category(group.getCategory())
                .build();
    }

    //== 수정 로직 ==//
    public void updateRecruitment(UpdateRecruitmentRequest requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.recruitmentStatus = requestDto.getRecruitmentStatus();
        this.lastModifiedTime = LocalDateTime.now();
    }
}
