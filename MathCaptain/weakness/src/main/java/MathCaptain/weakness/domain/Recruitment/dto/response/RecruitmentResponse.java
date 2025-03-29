package MathCaptain.weakness.domain.Recruitment.dto.response;

import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentResponse {

    private String authorName;

    private String recruitGroupName;

    private String title;

    private CategoryStatus category;

    private String content;

    private RecruitmentStatus recruitmentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private RecruitmentResponse(String authorName, String recruitGroupName, String title,
                                CategoryStatus category, String content, RecruitmentStatus recruitmentStatus,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.authorName = authorName;
        this.recruitGroupName = recruitGroupName;
        this.title = title;
        this.category = category;
        this.content = content;
        this.recruitmentStatus = recruitmentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RecruitmentResponse of(Recruitment recruitment) {
        return RecruitmentResponse.builder()
                .authorName(recruitment.getAuthor().getName())
                .recruitGroupName(recruitment.getRecruitGroup().getName())
                .title(recruitment.getTitle())
                .category(recruitment.getCategory())
                .content(recruitment.getContent())
                .recruitmentStatus(recruitment.getRecruitmentStatus())
                .createdAt(recruitment.getPostTime())
                .updatedAt(recruitment.getLastModifiedTime())
                .build();
    }
}
