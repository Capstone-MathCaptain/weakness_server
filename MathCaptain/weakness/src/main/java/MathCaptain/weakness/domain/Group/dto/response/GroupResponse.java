package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.common.enums.CategoryStatus;
import MathCaptain.weakness.domain.User.entity.Users;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupResponse {

    private Long groupId;

    private Long leaderId;

    private String leaderName;

    private String groupName;

    private CategoryStatus category;

    private int minDailyHours;

    private int minWeeklyDays;

    private Long groupPoint;

    private int groupRanking;

    private List<String> hashtags;

    private LocalDate created_date;

    private String groupImageUrl;

    @Builder
    private GroupResponse(Long groupId, Long leaderId, String leaderName, String groupName,
                          CategoryStatus category, int minDailyHours, int minWeeklyDays, Long groupPoint,
                          int groupRanking, List<String> hashtags, LocalDate created_date, String groupImageUrl) {
        this.groupId = groupId;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.groupName = groupName;
        this.category = category;
        this.minDailyHours = minDailyHours;
        this.minWeeklyDays = minWeeklyDays;
        this.groupPoint = groupPoint;
        this.groupRanking = groupRanking;
        this.hashtags = hashtags;
        this.created_date = created_date;
        this.groupImageUrl = groupImageUrl;
    }

    public static GroupResponse of(Users leader, Group group) {
        return GroupResponse.builder()
                .groupId(group.getId())
                .leaderId(leader.getUserId())
                .leaderName(leader.getName())
                .groupName(group.getName())
                .category(group.getCategory())
                .minDailyHours(group.getMinDailyHours())
                .minWeeklyDays(group.getMinWeeklyDays())
                .groupPoint(group.getGroupPoint())
                .groupRanking(group.getGroupRanking())
                .hashtags(group.getHashtags())
                .created_date(group.getCreateDate())
                .groupImageUrl(group.getGroupImageUrl())
                .build();
    }

    public static GroupResponse of(RelationBetweenUserAndGroup relation) {
        return GroupResponse.builder()
                .groupId(relation.getGroup().getId())
                .groupName(relation.getGroup().getName())
                .category(relation.getGroup().getCategory())
                .minDailyHours(relation.getGroup().getMinDailyHours())
                .minWeeklyDays(relation.getGroup().getMinWeeklyDays())
                .groupPoint(relation.getGroup().getGroupPoint())
                .hashtags(relation.getGroup().getHashtags())
                .groupImageUrl(relation.getGroup().getGroupImageUrl())
                .build();
    }
}
