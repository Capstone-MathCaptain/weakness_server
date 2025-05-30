package MathCaptain.weakness.domain.Recruitment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRecruitmentRequest {

    @NotNull(message = "제목을 입력해주세요!")
    @NotEmpty(message = "제목을 입력해주세요!")
    @Size(max = 30, message = "제목은 30자 이내로 작성해주세요!")
    private String title;

    @NotNull(message = "내용을 입력해주세요!")
    @NotEmpty(message = "내용을 입력해주세요!")
    private String content;

    private CreateRecruitmentRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static CreateRecruitmentRequest of(String title, String content) {
        return new CreateRecruitmentRequest(title, content);
    }
}
