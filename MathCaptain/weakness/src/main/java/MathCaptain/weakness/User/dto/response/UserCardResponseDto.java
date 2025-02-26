package MathCaptain.weakness.User.dto.response;

import MathCaptain.weakness.Group.dto.response.UserGroupCardResponseDto;
import MathCaptain.weakness.User.enums.Tiers;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCardResponseDto {

    private Long userId;

    private String userName;

    private Tiers userTier;

    private Long userPoint;

    private List<UserGroupCardResponseDto> groupCards;
}
