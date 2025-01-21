package MathCaptain.weakness.Recruitment.dto.request;

import MathCaptain.weakness.Recruitment.enums.RecruitmentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecruitmentRequestDto {

    @NotNull(message = "작성자를 입력해주세요!")
    @NotEmpty(message = "작성자를 입력해주세요!")
    private Long authorId;

    @NotNull(message = "그룹을 입력해주세요!")
    @NotEmpty(message = "그룹을 입력해주세요!")
    private Long recruitGroupId;

    @NotNull(message = "제목을 입력해주세요!")
    @NotEmpty(message = "제목을 입력해주세요!")
    @Size(max = 30, message = "제목은 30자 이내로 작성해주세요!")
    private String title;

    @NotNull(message = "내용을 입력해주세요!")
    @NotEmpty(message = "내용을 입력해주세요!")
    private String content;

    @NotNull(message = "모집 여부를 선택해주세요!")
    @NotEmpty(message = "모집 여부를 선택해주세요!")
    private RecruitmentStatus recruitmentStatus;
}
