package MathCaptain.weakness.domain.User.dto.response;

import MathCaptain.weakness.domain.Group.dto.response.GroupResponse;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
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

    private List<GroupResponse> joinedGroups;

    @Builder
    private UserResponse(Long userId, String email, String name, String nickname, Tiers tier, String phoneNumber, List<GroupResponse> joinedGroups) {
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

    public static UserResponse of(Users user, List<GroupResponse> joinedGroups) {
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

    public static UserResponse of(RelationBetweenUserAndGroup relation){
        return UserResponse.builder()
                .userId(relation.getMember().getUserId())
                .email(relation.getMember().getEmail())
                .name(relation.getMember().getName())
                .nickname(relation.getMember().getNickname())
                .phoneNumber(relation.getMember().getPhoneNumber())
                .build();
    }
}
