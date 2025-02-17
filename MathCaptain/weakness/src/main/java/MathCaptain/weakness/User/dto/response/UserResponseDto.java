package MathCaptain.weakness.User.dto.response;

import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.User.enums.Tiers;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponseDto {

    private Long userId;

    private String email;

    private String name;

    private String nickname;

    private Tiers tier;

    private String phoneNumber;

    private List<GroupResponseDto> joinedGroups;
}
