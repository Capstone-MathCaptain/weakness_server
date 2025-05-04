package MathCaptain.weakness.domain.Recruitment.dto.request;

import MathCaptain.weakness.domain.common.enums.RecruitmentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateRecruitmentRequest {

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
