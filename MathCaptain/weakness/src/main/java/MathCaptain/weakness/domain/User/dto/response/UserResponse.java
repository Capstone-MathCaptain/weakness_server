package MathCaptain.weakness.domain.User.dto.response;

import MathCaptain.weakness.domain.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.User.enums.Tiers;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private Long userId;

    private String email;

    private String name;

    private String nickname;

    private Tiers tier;

    private String phoneNumber;

    private List<GroupResponseDto> joinedGroups;

    @Builder
    private UserResponse(Long userId, String email, String name, String nickname, Tiers tier, String phoneNumber, List<GroupResponseDto> joinedGroups) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.tier = tier;
        this.phoneNumber = phoneNumber;
        this.joinedGroups = joinedGroups;
    }

    public static UserResponse of(Users user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .tier(user.getTier())
                .phoneNumber(user.getPhoneNumber())
                .joinedGroups(null)
                .build();
    }

    public static UserResponse of(Users user, List<GroupResponseDto> joinedGroups) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .tier(user.getTier())
                .phoneNumber(user.getPhoneNumber())
                .joinedGroups(joinedGroups)
                .build();
    }
}
