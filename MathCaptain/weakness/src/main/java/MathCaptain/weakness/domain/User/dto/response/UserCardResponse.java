package MathCaptain.weakness.domain.User.dto.response;

import MathCaptain.weakness.domain.Group.dto.response.UserGroupCardResponseDto;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.User.enums.Tiers;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCardResponse {

    private Long userId;

    private String userName;

    private Tiers userTier;

    private Long userPoint;

    private List<UserGroupCardResponseDto> groupCards;

    @Builder
    private UserCardResponse(Long userId, String userName, Tiers userTier, Long userPoint, List<UserGroupCardResponseDto> groupCards) {
        this.userId = userId;
        this.userName = userName;
        this.userTier = userTier;
        this.userPoint = userPoint;
        this.groupCards = groupCards;
    }

    public static UserCardResponse of(Users user) {
        return UserCardResponse.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .userTier(user.getTier())
                .userPoint(user.getUserPoint())
                .groupCards(null)
                .build();
    }

    public static UserCardResponse of(Users user, List<UserGroupCardResponseDto> groupCards) {
        return UserCardResponse.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .userTier(user.getTier())
                .userPoint(user.getUserPoint())
                .groupCards(groupCards)
                .build();
    }
}
