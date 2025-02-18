package MathCaptain.weakness.Recruitment.dto.response;

import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Recruitment.enums.RecruitmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecruitmentDetailResponseDto {

    private String authorName;

    private String recruitGroupName;

    private String title;

    private CategoryStatus category;

    private String content;

    private RecruitmentStatus recruitmentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CommentResponseDto> comments;
}
