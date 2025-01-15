package MathCaptain.weakness.Recruitment.dto.request;

import MathCaptain.weakness.Group.enums.CategoryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRecruitmentRequestDto {

    private Long authorId;

    private Long recruitGroupId;

    private CategoryStatus category;

    private String title;

    private String content;

}
