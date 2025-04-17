package MathCaptain.weakness.domain.Recruitment.dto.response;

import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentDetailResponse {

    private Long authorId;

    private Long recruitGroupId;

    private String authorName;

    private String recruitGroupName;

    private String title;

    private CategoryStatus category;

    private String content;

    private RecruitmentStatus recruitmentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CommentResponse> comments;

    @Builder
    private RecruitmentDetailResponse(Long authorId, Long recruitGroupId, String authorName, String recruitGroupName, String title,
                                      CategoryStatus category, String content, RecruitmentStatus recruitmentStatus,
                                      LocalDateTime createdAt, LocalDateTime updatedAt, List<CommentResponse> comments) {
        this.authorId = authorId;
        this.recruitGroupId = recruitGroupId;
        this.authorName = authorName;
        this.recruitGroupName = recruitGroupName;
        this.title = title;
        this.category = category;
        this.content = content;
        this.recruitmentStatus = recruitmentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.comments = comments;
    }

    public static RecruitmentDetailResponse of(Recruitment recruitment, List<CommentResponse> comments) {
        return RecruitmentDetailResponse.builder()
                .authorId(recruitment.getAuthor().getUserId())
                .recruitGroupId(recruitment.getRecruitGroup().getId())
                .authorName(recruitment.getAuthor().getName())
                .recruitGroupName(recruitment.getRecruitGroup().getName())
                .title(recruitment.getTitle())
                .category(recruitment.getCategory())
                .content(recruitment.getContent())
                .recruitmentStatus(recruitment.getRecruitmentStatus())
                .createdAt(recruitment.getPostTime())
                .updatedAt(recruitment.getLastModifiedTime())
                .comments(comments)
                .build();
    }
}
