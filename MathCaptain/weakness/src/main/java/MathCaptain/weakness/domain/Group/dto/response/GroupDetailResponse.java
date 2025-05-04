package MathCaptain.weakness.domain.Group.dto.response;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.common.enums.CategoryStatus;
import MathCaptain.weakness.domain.User.entity.Users;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupDetailResponse {

    private Long groupId;

    private String groupName;

    private CategoryStatus category;

    private Long leaderId;

    private String leaderName;

    private int minDailyHours;

    private int minWeeklyDays;

    private Long groupPoint;

    private int groupRanking;

    private List<String> hashtags;

    private String groupImageUrl;

    private Map<DayOfWeek, Integer> weeklyGoalAchieve;

    private Integer totalWeeklyGoalCount;

    private Long memberCount;

    @Builder
    private GroupDetailResponse(Long groupId, String groupName, CategoryStatus category, Long leaderId, String leaderName,
                                int minDailyHours, int minWeeklyDays, Long groupPoint, int groupRanking, List<String> hashtags,
                                String groupImageUrl, Map<DayOfWeek, Integer> weeklyGoalAchieve, Integer totalWeeklyGoalCount, Long memberCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.category = category;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.minDailyHours = minDailyHours;
        this.minWeeklyDays = minWeeklyDays;
        this.groupPoint = groupPoint;
        this.groupRanking = groupRanking;
        this.hashtags = hashtags;
        this.groupImageUrl = groupImageUrl;
        this.weeklyGoalAchieve = weeklyGoalAchieve;
        this.totalWeeklyGoalCount = totalWeeklyGoalCount;
        this.memberCount = memberCount;
    }

    public static GroupDetailResponse of(Users leader, Group group, Long memberCount, Integer totalWeeklyGoalCount) {
        return GroupDetailResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .category(group.getCategory())
                .leaderId(leader.getUserId())
                .leaderName(leader.getName())
                .minDailyHours(group.getMinDailyHours())
                .minWeeklyDays(group.getMinWeeklyDays())
                .groupPoint(group.getGroupPoint())
                .groupRanking(group.getGroupRanking())
                .hashtags(group.getHashtags())
                .groupImageUrl(group.getGroupImageUrl())
                .weeklyGoalAchieve(group.getWeeklyGoalAchieveMap())
                .totalWeeklyGoalCount(totalWeeklyGoalCount)
                .memberCount(memberCount)
                .build();
    }
}
