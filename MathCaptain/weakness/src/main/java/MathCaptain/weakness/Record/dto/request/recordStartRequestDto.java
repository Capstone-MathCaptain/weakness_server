package MathCaptain.weakness.Record.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class recordStartRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long groupId;

}
