package MathCaptain.weakness.Recruitment.dto.request;

import MathCaptain.weakness.Recruitment.enums.RecruitmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecruitmentRequestDto {

    private Long authorId;

    private Long recruitGroupId;

    private String title;

    private String content;

    private RecruitmentStatus recruitmentStatus;
}
